document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("register-form");
    const message = document.getElementById("register-message");

    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        const login = document.getElementById("login").value;
        const password = document.getElementById("password").value;
        const confirmPassword = document.getElementById("confirmPassword").value;

        if (password !== confirmPassword) {
            message.textContent = "Hasła nie są takie same.";
            message.className = "error-message";
            return;
        }

        try {
            const response = await fetch("/auth/register/client", {
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
                message.textContent = data.message || "Nie udało się utworzyć konta.";
                message.className = "error-message";
                return;
            }

            message.textContent = "Konto zostało utworzone. Za chwilę przejdziesz do logowania.";
            message.className = "success-message";

            form.reset();

            setTimeout(() => {
                window.location.href = "/login.html";
            }, 1500);

        } catch (error) {
            message.textContent = "Błąd połączenia z serwerem.";
            message.className = "error-message";
        }
    });
});