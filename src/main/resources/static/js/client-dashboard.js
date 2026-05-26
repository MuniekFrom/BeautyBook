const token = localStorage.getItem("token");
const role = localStorage.getItem("role");
const login = localStorage.getItem("login");

if (!token || role !== "CLIENT") {
    window.location.href = "/login.html";
}

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("client-login").textContent = "Zalogowano jako: " + login;

    document.getElementById("logout-button").addEventListener("click", logout);
    document.getElementById("booking-form").addEventListener("submit", bookAppointment);

    document.getElementById("serviceSelect").addEventListener("change", () => {
        const serviceId = document.getElementById("serviceSelect").value;

        if (!serviceId) {
            document.getElementById("slotSelect").innerHTML = '<option value="">Najpierw wybierz usługę</option>';
            document.getElementById("slots-preview").innerHTML = "<p>Wybierz usługę, aby zobaczyć pasujące terminy.</p>";
            return;
        }

        loadSlotsByService(serviceId);
    });

    loadServices();
    loadMyAppointments();
});

function logout() {
    localStorage.clear();
    window.location.href = "/login.html";
}

async function readErrorMessage(response, defaultMessage) {
    try {
        const data = await response.json();
        return data.message || defaultMessage;
    } catch (error) {
        return defaultMessage;
    }
}

async function loadServices() {
    const serviceSelect = document.getElementById("serviceSelect");

    try {
        const response = await fetch("/services");

        if (!response.ok) {
            serviceSelect.innerHTML = '<option value="">Błąd pobierania usług</option>';
            return;
        }

        const services = await response.json();

        if (services.length === 0) {
            serviceSelect.innerHTML = '<option value="">Brak usług</option>';
            return;
        }

        serviceSelect.innerHTML = '<option value="">Wybierz usługę</option>';

        services.forEach(service => {
            const option = document.createElement("option");
            option.value = service.id;

            const categoryName = service.category ? service.category.name : "Brak kategorii";

            option.textContent = `${service.name} - ${service.price} zł (${service.durationMinutes} min) | ${categoryName}`;
            serviceSelect.appendChild(option);
        });

    } catch (error) {
        serviceSelect.innerHTML = '<option value="">Błąd pobierania usług</option>';
    }
}

async function loadSlotsByService(serviceId) {
    const slotSelect = document.getElementById("slotSelect");
    const slotsPreview = document.getElementById("slots-preview");

    slotSelect.innerHTML = '<option value="">Ładowanie terminów...</option>';
    slotsPreview.innerHTML = "<p>Ładowanie terminów...</p>";

    try {
        const response = await fetch(`/slots/available/service/${serviceId}`);

        if (!response.ok) {
            slotSelect.innerHTML = '<option value="">Błąd pobierania terminów</option>';
            slotsPreview.innerHTML = "<p>Nie udało się pobrać terminów.</p>";
            return;
        }

        const slots = await response.json();

        if (slots.length === 0) {
            slotSelect.innerHTML = '<option value="">Brak pasujących terminów</option>';
            slotsPreview.innerHTML = "<p>Brak wolnych terminów dla tej usługi.</p>";
            return;
        }

        slotSelect.innerHTML = '<option value="">Wybierz termin</option>';

        slots.forEach(slot => {
            const option = document.createElement("option");
            option.value = slot.id;
            option.textContent = `${formatDate(slot.startTime)} | ${slot.employeeName}`;
            slotSelect.appendChild(option);
        });

        slotsPreview.innerHTML = slots.map(slot => `
            <div class="list-item">
                <strong>${slot.employeeName}</strong>
                <span>${formatDate(slot.startTime)}</span>
                <small>Status: ${translateSlotStatus(slot.status)}</small>
            </div>
        `).join("");

    } catch (error) {
        slotSelect.innerHTML = '<option value="">Błąd pobierania terminów</option>';
        slotsPreview.innerHTML = "<p>Nie udało się pobrać terminów.</p>";
    }
}

async function bookAppointment(event) {
    event.preventDefault();

    const message = document.getElementById("booking-message");

    const serviceId = document.getElementById("serviceSelect").value;
    const slotId = document.getElementById("slotSelect").value;
    const notes = document.getElementById("notes").value;

    if (!serviceId || !slotId) {
        message.textContent = "Wybierz usługę i termin.";
        message.className = "error-message";
        return;
    }

    try {
        const response = await fetch("/client/appointments/book", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            },
            body: JSON.stringify({
                serviceId: Number(serviceId),
                slotId: Number(slotId),
                notes: notes
            })
        });

        if (!response.ok) {
            message.textContent = await readErrorMessage(response, "Nie udało się zarezerwować wizyty.");
            message.className = "error-message";
            return;
        }

        message.textContent = "Wizyta została zarezerwowana.";
        message.className = "success-message";

        document.getElementById("booking-form").reset();
        document.getElementById("slotSelect").innerHTML = '<option value="">Najpierw wybierz usługę</option>';
        document.getElementById("slots-preview").innerHTML = "<p>Wybierz usługę, aby zobaczyć pasujące terminy.</p>";

        await loadMyAppointments();

    } catch (error) {
        message.textContent = "Błąd połączenia z serwerem.";
        message.className = "error-message";
    }
}

async function loadMyAppointments() {
    const container = document.getElementById("appointments-container");

    try {
        const response = await fetch("/client/appointments/me", {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!response.ok) {
            container.innerHTML = "<p>Nie udało się pobrać wizyt.</p>";
            return;
        }

        const appointments = await response.json();

        if (appointments.length === 0) {
            container.innerHTML = "<p>Nie masz jeszcze żadnych wizyt.</p>";
            return;
        }

        container.innerHTML = appointments.map(appointment => `
            <div class="list-item">
                <strong>${appointment.serviceName}</strong>
                <span>${appointment.employeeName}</span>
                <span>${formatDate(appointment.startTime)} - ${formatDate(appointment.endTime)}</span>
                <small>Status: ${translateAppointmentStatus(appointment.status)}</small>

                ${appointment.status === "BOOKED" ? `
                    <button class="small-danger-button" onclick="cancelAppointment(${appointment.id})">
                        Anuluj
                    </button>
                ` : ""}
            </div>
        `).join("");

    } catch (error) {
        container.innerHTML = "<p>Błąd połączenia z serwerem.</p>";
    }
}

async function cancelAppointment(appointmentId) {
    if (!confirm("Czy na pewno chcesz anulować tę wizytę?")) {
        return;
    }

    try {
        const response = await fetch(`/client/appointments/${appointmentId}`, {
            method: "DELETE",
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!response.ok) {
            alert("Nie udało się anulować wizyty.");
            return;
        }

        await loadMyAppointments();

        const serviceId = document.getElementById("serviceSelect").value;
        if (serviceId) {
            await loadSlotsByService(serviceId);
        }

    } catch (error) {
        alert("Błąd połączenia z serwerem.");
    }
}

function formatDate(dateTime) {
    const date = new Date(dateTime);

    return date.toLocaleString("pl-PL", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit"
    });
}

function translateSlotStatus(status) {
    switch (status) {
        case "AVAILABLE":
            return "Wolny";
        case "BOOKED":
            return "Zarezerwowany";
        case "CANCELLED":
            return "Anulowany";
        default:
            return status;
    }
}

function translateAppointmentStatus(status) {
    switch (status) {
        case "BOOKED":
            return "Zarezerwowana";
        case "COMPLETED":
            return "Wykonana";
        case "CANCELLED":
            return "Anulowana";
        default:
            return status;
    }
}