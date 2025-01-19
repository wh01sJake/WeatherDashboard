// Base API URL (Backend API Endpoint)
const BASE_API_URL = window.location.origin + "/api";

// Select form elements
const loginForm = document.getElementById("loginForm");

// Handle Login Form Submission
loginForm.addEventListener("submit", async (e) => {
    e.preventDefault(); // Prevent default form submission
    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value;

    try {
        // Show loading indicator (optional)
        loginForm.insertAdjacentHTML("beforeend", '<p id="loading">Logging in...</p>');

        // Send login request to the backend
        const response = await fetch(`${BASE_API_URL}/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ username, password }),
        });

        // Remove loading message
        document.getElementById("loading")?.remove();

        if (response.ok) {
            const data = await response.json(); // Expecting { token: "JWT_TOKEN" } from backend
            sessionStorage.setItem("token", data.token); // Store token in sessionStorage
            alert("Login successful!");
            window.location.href = "dashboard.html"; // Redirect on success
        } else if (response.status === 401) {
            alert("Invalid username or password. Please try again.");
        } else {
            alert("An unexpected error occurred. Please try again later.");
        }
    } catch (error) {
        console.error("Login error:", error);
        alert("Unable to login. Please check your connection.");
    }
});