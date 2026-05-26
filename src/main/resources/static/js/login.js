document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.getElementById("login-form");
    const errorMessage = document.getElementById("error-message");

    loginForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        const login = document.getElementById("login").value;
        const password = document.getElementById("password").value;

        try {
            const response = await fetch("/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    login: login,
                    password: password
                })
            });

            const data = await response.json();

            if (!response.ok) {
                errorMessage.textContent = data.message || "Nieprawidłowy login lub hasło.";
                return;
            }

            localStorage.setItem("token", data.token);
            localStorage.setItem("login", data.login);
            localStorage.setItem("role", data.role);
            localStorage.setItem("userId", data.id);

            if (data.role === "CLIENT") {
                window.location.href = "/client-dashboard.html";
            } else if (data.role === "EMPLOYEE") {
                window.location.href = "/employee-dashboard.html";
            } else if (data.role === "ADMIN") {
                window.location.href = "/admin-dashboard.html";
            } else {
                errorMessage.textContent = "Nieznana rola użytkownika.";
            }

        } catch (error) {
            errorMessage.textContent = "Wystąpił błąd połączenia z serwerem.";
        }
    });
});