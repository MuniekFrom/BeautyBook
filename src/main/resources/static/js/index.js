document.addEventListener("DOMContentLoaded", () => {
    loadServices();
    loadEmployees();
});

async function loadServices() {
    const container = document.getElementById("services-container");

    try {
        const response = await fetch("/services");
        const services = await response.json();

        if (services.length === 0) {
            container.innerHTML = "<p>Brak usług do wyświetlenia.</p>";
            return;
        }

        container.innerHTML = services.map(service => {
            const categoryName = service.category ? service.category.name : "Brak kategorii";

            return `
                <div class="card">
                    <h3>${service.name}</h3>
                    <p>${service.description || ""}</p>
                    <strong>${service.price} zł</strong>
                    <span class="badge">${service.durationMinutes} min · ${categoryName}</span>
                </div>
            `;
        }).join("");

    } catch (error) {
        container.innerHTML = "<p>Nie udało się pobrać usług.</p>";
    }
}

async function loadEmployees() {
    const container = document.getElementById("employees-container");

    try {
        const response = await fetch("/employees");
        const employees = await response.json();

        if (employees.length === 0) {
            container.innerHTML = "<p>Brak pracowników do wyświetlenia.</p>";
            return;
        }

        container.innerHTML = employees.map(employee => `
            <div class="card">
                <h3>${employee.firstName} ${employee.lastName}</h3>
                <p>${employee.specialization}</p>
                <p>${employee.description || ""}</p>
                <span class="badge">${employee.categoryName || "Brak kategorii"}</span>
            </div>
        `).join("");

    } catch (error) {
        container.innerHTML = "<p>Nie udało się pobrać pracowników.</p>";
    }
}