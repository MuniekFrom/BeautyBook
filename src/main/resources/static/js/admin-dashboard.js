const token = localStorage.getItem("token");
const role = localStorage.getItem("role");
const login = localStorage.getItem("login");

let currentAppointmentFilter = "ALL";
let cachedAppointments = [];
let cachedAvailableSlots = [];

if (!token || role !== "ADMIN") {
    window.location.href = "/login.html";
}

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("admin-login").textContent = "Zalogowano jako: " + login;

    document.getElementById("logout-button").addEventListener("click", logout);

    document.getElementById("category-form").addEventListener("submit", createCategory);
    document.getElementById("service-form").addEventListener("submit", saveService);
    document.getElementById("service-cancel-edit-button").addEventListener("click", cancelServiceEdit);
    document.getElementById("employee-full-form").addEventListener("submit", createEmployeeWithProfile);

    document.querySelectorAll(".filter-button").forEach(button => {
        button.addEventListener("click", async () => {
            document.querySelectorAll(".filter-button").forEach(btn => btn.classList.remove("active"));
            button.classList.add("active");

            currentAppointmentFilter = button.dataset.filter;

            if (currentAppointmentFilter === "AVAILABLE") {
                renderAvailableSlots();
            } else {
                renderAppointments();
            }
        });
    });

    refreshAdminDashboard();
});

function logout() {
    localStorage.clear();
    window.location.href = "/login.html";
}

async function refreshAdminDashboard() {
    await loadStatistics();
    await loadCategories();
    await loadUsers();
    await loadServices();
    await loadEmployees();
    await loadAppointmentsAndSlots();
}

async function readErrorMessage(response, defaultMessage) {
    try {
        const data = await response.json();
        return data.message || defaultMessage;
    } catch (error) {
        return defaultMessage;
    }
}

async function loadStatistics() {
    try {
        const response = await fetch("/admin/statistics", {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!response.ok) {
            return;
        }

        const stats = await response.json();

        document.getElementById("services-count").textContent = stats.servicesCount;
        document.getElementById("employees-count").textContent = stats.employeesCount;
        document.getElementById("appointments-count").textContent = stats.appointmentsCount;
        document.getElementById("booked-count").textContent = stats.bookedAppointmentsCount;
        document.getElementById("completed-count").textContent = stats.completedAppointmentsCount;
        document.getElementById("cancelled-count").textContent = stats.cancelledAppointmentsCount;

    } catch (error) {
        console.error("Błąd pobierania statystyk", error);
    }
}

async function loadCategories() {
    const serviceCategorySelect = document.getElementById("category");
    const employeeCategorySelect = document.getElementById("employeeCategory");
    const container = document.getElementById("admin-categories-container");

    try {
        const response = await fetch("/categories");

        if (!response.ok) {
            serviceCategorySelect.innerHTML = '<option value="">Błąd pobierania kategorii</option>';
            employeeCategorySelect.innerHTML = '<option value="">Błąd pobierania kategorii</option>';
            container.innerHTML = "<p>Nie udało się pobrać kategorii.</p>";
            return;
        }

        const categories = await response.json();

        if (categories.length === 0) {
            serviceCategorySelect.innerHTML = '<option value="">Brak kategorii</option>';
            employeeCategorySelect.innerHTML = '<option value="">Brak kategorii</option>';
            container.innerHTML = "<p>Brak kategorii. Dodaj pierwszą kategorię.</p>";
            return;
        }

        serviceCategorySelect.innerHTML = '<option value="">Wybierz kategorię</option>';
        employeeCategorySelect.innerHTML = '<option value="">Wybierz kategorię</option>';

        categories.forEach(category => {
            const serviceOption = document.createElement("option");
            serviceOption.value = category.id;
            serviceOption.textContent = category.name;
            serviceCategorySelect.appendChild(serviceOption);

            const employeeOption = document.createElement("option");
            employeeOption.value = category.id;
            employeeOption.textContent = category.name;
            employeeCategorySelect.appendChild(employeeOption);
        });

        container.innerHTML = categories.map(category => `
            <div class="list-item">
                <strong>${category.name}</strong>
                <small>ID: ${category.id}</small>

                <button class="small-danger-button" onclick="deleteCategory(${category.id})">
                    Usuń kategorię
                </button>
            </div>
        `).join("");

    } catch (error) {
        serviceCategorySelect.innerHTML = '<option value="">Błąd połączenia</option>';
        employeeCategorySelect.innerHTML = '<option value="">Błąd połączenia</option>';
        container.innerHTML = "<p>Błąd połączenia z serwerem.</p>";
    }
}

async function createCategory(event) {
    event.preventDefault();

    const message = document.getElementById("category-message");

    const body = {
        name: document.getElementById("categoryName").value
    };

    try {
        const response = await fetch("/admin/categories", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            },
            body: JSON.stringify(body)
        });

        if (!response.ok) {
            message.textContent = await readErrorMessage(response, "Nie udało się dodać kategorii.");
            message.className = "error-message";
            return;
        }

        message.textContent = "Kategoria została dodana.";
        message.className = "success-message";

        document.getElementById("category-form").reset();

        await loadCategories();

    } catch (error) {
        message.textContent = "Błąd połączenia z serwerem.";
        message.className = "error-message";
    }
}

async function deleteCategory(categoryId) {
    if (!confirm("Czy na pewno chcesz usunąć tę kategorię? Jeśli jest używana przez usługę lub pracownika, usunięcie może się nie udać.")) {
        return;
    }

    try {
        const response = await fetch(`/admin/categories/${categoryId}`, {
            method: "DELETE",
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!response.ok) {
            alert("Nie udało się usunąć kategorii. Możliwe, że jest przypisana do usługi albo pracownika.");
            return;
        }

        await loadCategories();

    } catch (error) {
        alert("Błąd połączenia z serwerem.");
    }
}

async function loadUsers() {
    const container = document.getElementById("admin-users-container");

    try {
        const response = await fetch("/admin/users", {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!response.ok) {
            container.innerHTML = "<p>Nie udało się pobrać użytkowników.</p>";
            return;
        }

        const users = await response.json();

        if (users.length === 0) {
            container.innerHTML = "<p>Brak użytkowników.</p>";
            return;
        }

        container.innerHTML = users.map(user => `
            <div class="list-item">
                <strong>${user.login}</strong>
                <span>Rola: ${translateRole(user.role)}</span>
                <small>ID: ${user.id} | Status: ${user.active ? "Aktywne" : "Nieaktywne"}</small>

                ${user.role !== "ADMIN" ? `
                    ${user.active ? `
                        <button class="small-danger-button" onclick="deactivateUser(${user.id})">
                            Dezaktywuj konto
                        </button>
                    ` : `
                        <button class="small-success-button" onclick="activateUser(${user.id})">
                            Aktywuj konto
                        </button>

                        <button class="small-danger-button" onclick="deleteInactiveUser(${user.id})">
                            Usuń konto
                        </button>
                    `}
                ` : ""}
            </div>
        `).join("");

    } catch (error) {
        container.innerHTML = "<p>Błąd połączenia z serwerem.</p>";
    }
}

async function deactivateUser(userId) {
    if (!confirm("Czy na pewno chcesz dezaktywować to konto?")) {
        return;
    }

    try {
        const response = await fetch(`/admin/users/${userId}/deactivate`, {
            method: "PATCH",
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!response.ok) {
            alert("Nie udało się dezaktywować konta.");
            return;
        }

        await loadUsers();

    } catch (error) {
        alert("Błąd połączenia z serwerem.");
    }
}

async function activateUser(userId) {
    try {
        const response = await fetch(`/admin/users/${userId}/activate`, {
            method: "PATCH",
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!response.ok) {
            alert("Nie udało się aktywować konta.");
            return;
        }

        await loadUsers();

    } catch (error) {
        alert("Błąd połączenia z serwerem.");
    }
}

async function deleteInactiveUser(userId) {
    if (!confirm("Czy na pewno chcesz usunąć to konto? Ten email będzie można zarejestrować ponownie.")) {
        return;
    }

    try {
        const response = await fetch(`/admin/users/${userId}`, {
            method: "DELETE",
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!response.ok) {
            alert("Nie udało się usunąć konta. Konto musi być najpierw dezaktywowane.");
            return;
        }

        await loadUsers();

    } catch (error) {
        alert("Błąd połączenia z serwerem.");
    }
}

async function saveService(event) {
    event.preventDefault();

    const message = document.getElementById("service-message");
    const serviceId = document.getElementById("serviceId").value;

    const body = {
        name: document.getElementById("serviceName").value,
        description: document.getElementById("serviceDescription").value,
        price: Number(document.getElementById("servicePrice").value),
        durationMinutes: Number(document.getElementById("durationMinutes").value),
        categoryId: Number(document.getElementById("category").value)
    };

    const isEditMode = serviceId !== "";
    const url = isEditMode ? `/admin/services/${serviceId}` : "/admin/services";
    const method = isEditMode ? "PUT" : "POST";

    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            },
            body: JSON.stringify(body)
        });

        if (!response.ok) {
            message.textContent = await readErrorMessage(response, "Nie udało się zapisać usługi.");
            message.className = "error-message";
            return;
        }

        message.textContent = isEditMode
            ? "Usługa została zaktualizowana."
            : "Usługa została dodana.";

        message.className = "success-message";

        document.getElementById("service-form").reset();
        cancelServiceEdit();

        await loadServices();
        await loadStatistics();

    } catch (error) {
        message.textContent = "Błąd połączenia z serwerem.";
        message.className = "error-message";
    }
}

function editService(service) {
    document.getElementById("serviceId").value = service.id;
    document.getElementById("serviceName").value = service.name;
    document.getElementById("serviceDescription").value = service.description || "";
    document.getElementById("servicePrice").value = service.price;
    document.getElementById("durationMinutes").value = service.durationMinutes;

    const categoryId = service.category ? service.category.id : "";
    document.getElementById("category").value = categoryId;

    document.getElementById("service-form-title").textContent = "Edytuj usługę";
    document.getElementById("service-submit-button").textContent = "Zapisz zmiany";
    document.getElementById("service-cancel-edit-button").style.display = "block";

    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
}

function cancelServiceEdit() {
    document.getElementById("serviceId").value = "";
    document.getElementById("service-form-title").textContent = "Dodaj usługę";
    document.getElementById("service-submit-button").textContent = "Dodaj usługę";
    document.getElementById("service-cancel-edit-button").style.display = "none";
}

async function createEmployeeWithProfile(event) {
    event.preventDefault();

    const message = document.getElementById("employee-full-message");

    const body = {
        login: document.getElementById("employeeLogin").value,
        password: document.getElementById("employeePassword").value,
        firstName: document.getElementById("employeeFirstName").value,
        lastName: document.getElementById("employeeLastName").value,
        specialization: document.getElementById("employeeSpecialization").value,
        categoryId: Number(document.getElementById("employeeCategory").value),
        description: document.getElementById("employeeDescription").value
    };

    try {
        const response = await fetch("/admin/employees/create", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            },
            body: JSON.stringify(body)
        });

        if (!response.ok) {
            message.textContent = await readErrorMessage(response, "Nie udało się dodać pracownika.");
            message.className = "error-message";
            return;
        }

        message.textContent = "Pracownik został dodany.";
        message.className = "success-message";

        document.getElementById("employee-full-form").reset();

        await loadUsers();
        await loadEmployees();
        await loadStatistics();

    } catch (error) {
        message.textContent = "Błąd połączenia z serwerem.";
        message.className = "error-message";
    }
}

async function loadServices() {
    const container = document.getElementById("admin-services-container");

    try {
        const response = await fetch("/services");

        if (!response.ok) {
            container.innerHTML = "<p>Nie udało się pobrać usług.</p>";
            return;
        }

        const services = await response.json();

        if (services.length === 0) {
            container.innerHTML = "<p>Brak usług.</p>";
            return;
        }

        container.innerHTML = services.map(service => `
            <div class="list-item">
                <strong>${service.name}</strong>
                <span>${service.description || ""}</span>
                <span>${service.price} zł · ${service.durationMinutes} min</span>
                <small>Kategoria: ${service.category ? service.category.name : "Brak"} | ID: ${service.id}</small>

                <button class="small-edit-button" onclick='editService(${JSON.stringify(service)})'>
                    Edytuj usługę
                </button>

                <button class="small-danger-button" onclick="deleteService(${service.id})">
                    Usuń usługę
                </button>
            </div>
        `).join("");

    } catch (error) {
        container.innerHTML = "<p>Nie udało się pobrać usług.</p>";
    }
}

async function deleteService(serviceId) {
    if (!confirm("Czy na pewno chcesz usunąć tę usługę?")) {
        return;
    }

    try {
        const response = await fetch(`/admin/services/${serviceId}`, {
            method: "DELETE",
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!response.ok) {
            alert("Nie udało się usunąć usługi. Możliwe, że usługa ma już przypisane wizyty.");
            return;
        }

        await loadServices();
        await loadStatistics();

    } catch (error) {
        alert("Błąd połączenia z serwerem.");
    }
}

async function loadEmployees() {
    const container = document.getElementById("admin-employees-container");

    try {
        const response = await fetch("/employees");

        if (!response.ok) {
            container.innerHTML = "<p>Nie udało się pobrać pracowników.</p>";
            return;
        }

        const employees = await response.json();

        if (employees.length === 0) {
            container.innerHTML = "<p>Brak profili pracowników.</p>";
            return;
        }

        container.innerHTML = employees.map(employee => `
            <div class="list-item">
                <strong>${employee.firstName} ${employee.lastName}</strong>
                <span>${employee.specialization}</span>
                <span>${employee.description || ""}</span>
                <small>
                    Kategoria: ${employee.categoryName || "Brak"}
                    | Login: ${employee.login}
                    | User ID: ${employee.userId}
                    | Profile ID: ${employee.id}
                </small>
            </div>
        `).join("");

    } catch (error) {
        container.innerHTML = "<p>Nie udało się pobrać pracowników.</p>";
    }
}

async function loadAppointmentsAndSlots() {
    const container = document.getElementById("admin-appointments-container");

    try {
        const appointmentsResponse = await fetch("/admin/appointments", {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!appointmentsResponse.ok) {
            container.innerHTML = "<p>Nie udało się pobrać wizyt.</p>";
            return;
        }

        cachedAppointments = await appointmentsResponse.json();

        const slotsResponse = await fetch("/slots/available");

        if (slotsResponse.ok) {
            cachedAvailableSlots = await slotsResponse.json();
        } else {
            cachedAvailableSlots = [];
        }

        if (currentAppointmentFilter === "AVAILABLE") {
            renderAvailableSlots();
        } else {
            renderAppointments();
        }

    } catch (error) {
        container.innerHTML = "<p>Błąd połączenia z serwerem.</p>";
    }
}

function renderAppointments() {
    const container = document.getElementById("admin-appointments-container");

    if (currentAppointmentFilter === "ALL") {
        const appointmentHtml = cachedAppointments.map(appointment => `
            <div class="list-item">
                <strong>${appointment.serviceName}</strong>
                <span>Klientka: ${appointment.clientLogin}</span>
                <span>Pracownik: ${appointment.employeeName}</span>
                <span>Termin: ${formatDate(appointment.startTime)}</span>
                <small>Status: ${translateStatus(appointment.status)}</small>
                ${appointment.notes ? `<p>Notatka: ${appointment.notes}</p>` : ""}
            </div>
        `).join("");

        const availableSlotsHtml = cachedAvailableSlots.map(slot => `
            <div class="list-item">
                <strong>Wolny termin</strong>
                <span>Pracownik: ${slot.employeeName}</span>
                <span>Termin: ${formatDate(slot.startTime)}</span>
                <small>Status: Wolny</small>
            </div>
        `).join("");

        const html = appointmentHtml + availableSlotsHtml;

        container.innerHTML = html || "<p>Brak wizyt i wolnych terminów.</p>";
        return;
    }

    const appointmentsToShow = cachedAppointments.filter(appointment =>
        appointment.status === currentAppointmentFilter
    );

    if (appointmentsToShow.length === 0) {
        container.innerHTML = "<p>Brak wizyt w tej kategorii.</p>";
        return;
    }

    container.innerHTML = appointmentsToShow.map(appointment => `
        <div class="list-item">
            <strong>${appointment.serviceName}</strong>
            <span>Klientka: ${appointment.clientLogin}</span>
            <span>Pracownik: ${appointment.employeeName}</span>
            <span>Termin: ${formatDate(appointment.startTime)}</span>
            <small>Status: ${translateStatus(appointment.status)}</small>
            ${appointment.notes ? `<p>Notatka: ${appointment.notes}</p>` : ""}
        </div>
    `).join("");
}

function renderAvailableSlots() {
    const container = document.getElementById("admin-appointments-container");

    if (cachedAvailableSlots.length === 0) {
        container.innerHTML = "<p>Brak wolnych terminów.</p>";
        return;
    }

    container.innerHTML = cachedAvailableSlots.map(slot => `
        <div class="list-item">
            <strong>Wolny termin</strong>
            <span>Pracownik: ${slot.employeeName}</span>
            <span>Termin: ${formatDate(slot.startTime)}</span>
            <small>Status: Wolny</small>
        </div>
    `).join("");
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

function translateStatus(status) {
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

function translateRole(role) {
    switch (role) {
        case "CLIENT":
            return "Klient";
        case "EMPLOYEE":
            return "Pracownik";
        case "ADMIN":
            return "Administrator";
        default:
            return role;
    }
}