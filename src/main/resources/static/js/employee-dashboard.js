const token = localStorage.getItem("token");
const role = localStorage.getItem("role");
const login = localStorage.getItem("login");

if (!token || role !== "EMPLOYEE") {
    window.location.href = "/login.html";
}

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("employee-login").textContent = "Zalogowano jako: " + login;

    document.getElementById("logout-button").addEventListener("click", logout);
    document.getElementById("slot-form").addEventListener("submit", createSlot);
    document.getElementById("change-password-form").addEventListener("submit", changePassword);

    loadMySlots();
    loadEmployeeAppointments();
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

async function createSlot(event) {
    event.preventDefault();

    const startTime = document.getElementById("startTime").value;
    const message = document.getElementById("slot-message");

    if (!startTime) {
        message.textContent = "Wybierz początek terminu.";
        message.className = "error-message";
        return;
    }

    const selectedDate = new Date(startTime);
    const now = new Date();

    if (selectedDate < now) {
        message.textContent = "Nie można dodać terminu w przeszłości.";
        message.className = "error-message";
        return;
    }

    try {
        const response = await fetch("/employee/slots", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            },
            body: JSON.stringify({
                startTime: startTime
            })
        });

        if (!response.ok) {
            message.textContent = await readErrorMessage(response, "Nie udało się dodać terminu.");
            message.className = "error-message";
            return;
        }

        message.textContent = "Termin został dodany.";
        message.className = "success-message";

        document.getElementById("slot-form").reset();

        await loadMySlots();

    } catch (error) {
        message.textContent = "Błąd połączenia z serwerem.";
        message.className = "error-message";
    }
}

async function loadMySlots() {
    const container = document.getElementById("employee-slots-container");

    try {
        const response = await fetch("/employee/slots/me", {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!response.ok) {
            container.innerHTML = "<p>Nie udało się pobrać terminów.</p>";
            return;
        }

        const slots = await response.json();

        if (slots.length === 0) {
            container.innerHTML = "<p>Nie masz jeszcze dodanych terminów.</p>";
            return;
        }

        container.innerHTML = slots.map(slot => `
            <div class="list-item">
                <strong>${formatDate(slot.startTime)}</strong>
                <span>Pracownik: ${slot.employeeName}</span>
                <small>Status: ${translateSlotStatus(slot.status)}</small>
            </div>
        `).join("");

    } catch (error) {
        container.innerHTML = "<p>Błąd połączenia z serwerem.</p>";
    }
}

async function loadEmployeeAppointments() {
    const container = document.getElementById("employee-appointments-container");

    try {
        const response = await fetch("/employee/appointments/me", {
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
                <span>Klientka: ${appointment.clientLogin}</span>
                <span>Termin: ${formatDate(appointment.startTime)} - ${formatDate(appointment.endTime)}</span>
                <small>Status: ${translateAppointmentStatus(appointment.status)}</small>

                ${appointment.notes ? `<p>Notatka: ${appointment.notes}</p>` : ""}

                ${appointment.status === "BOOKED" ? `
                    <button class="small-success-button" onclick="completeAppointment(${appointment.id})">
                        Oznacz jako wykonaną
                    </button>
                ` : ""}
            </div>
        `).join("");

    } catch (error) {
        container.innerHTML = "<p>Błąd połączenia z serwerem.</p>";
    }
}

async function completeAppointment(appointmentId) {
    if (!confirm("Czy oznaczyć wizytę jako wykonaną?")) {
        return;
    }

    try {
        const response = await fetch(`/employee/appointments/${appointmentId}/complete`, {
            method: "PATCH",
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!response.ok) {
            alert("Nie udało się zmienić statusu wizyty.");
            return;
        }

        await loadEmployeeAppointments();
        await loadMySlots();

    } catch (error) {
        alert("Błąd połączenia z serwerem.");
    }
}

async function changePassword(event) {
    event.preventDefault();

    const message = document.getElementById("password-message");

    const currentPassword = document.getElementById("currentPassword").value;
    const newPassword = document.getElementById("newPassword").value;

    try {
        const response = await fetch("/auth/change-password", {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            },
            body: JSON.stringify({
                currentPassword: currentPassword,
                newPassword: newPassword
            })
        });

        if (!response.ok) {
            message.textContent = await readErrorMessage(response, "Nie udało się zmienić hasła.");
            message.className = "error-message";
            return;
        }

        message.textContent = "Hasło zostało zmienione. Przy następnym logowaniu użyj nowego hasła.";
        message.className = "success-message";

        document.getElementById("change-password-form").reset();

    } catch (error) {
        message.textContent = "Błąd połączenia z serwerem.";
        message.className = "error-message";
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