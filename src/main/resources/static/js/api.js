// Sends a fetch request with the saved login credentials attached, so admin.js and user.js don't have to repeat this every time.
function authFetch(url, options = {}) {
    options.headers = {
        ...options.headers,
        "Authorization": sessionStorage.getItem("authHeader")
    };
    return fetch(url, options);
}
