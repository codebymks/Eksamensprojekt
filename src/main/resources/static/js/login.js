// Handles the login form on index.html: sends the typed credentials to the backend, then sends the user to their page, or shows an error if the credentials were wrong.
document.getElementById("login-form").addEventListener("submit", function (event) {
    event.preventDefault();

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const authHeader = "Basic " + btoa(username + ":" + password);
    const errorMessage = document.getElementById("login-error");

    fetch("/api/login", {
        headers: {"Authorization": authHeader}
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Invalid username or password");
            }
            return response.json();
        })
        .then(data => {
            //Save the header so the user/admin pages can send it with their own fetch calls.
            sessionStorage.setItem("authHeader", authHeader);
            window.location.href = data.role === "ROLE_ADMIN" ? "html/admin.html" : "html/user.html";
        })
        .catch(() => {
            errorMessage.textContent = "Wrong password or username. Please try again";
        });
});