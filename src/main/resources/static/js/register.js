// Base API URL (Backend API Endpoint)
const BASE_API_URL = window.location.origin + "/api";



document.addEventListener("DOMContentLoaded", () => {
    const registerForm = document.getElementById("registerForm");

    registerForm.addEventListener("submit", async (event) => {
        event.preventDefault(); // Prevent form submission from reloading the page

        const username = document.getElementById("newUsername").value.trim();
        const password = document.getElementById("newPassword").value.trim();

        // Verify user input before submitting
        if (!username || !password) {
            alert("Please fill in both username and password.");
            return;
        }

        try {
            // Send the registration data to the backend API
            const response = await fetch(`${BASE_API_URL}/register`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ username, password }), // Send JSON object
            });

            // Handle the response
            if (response.ok) {
                alert("Registration successful! Redirecting to login page...");
                window.location.href = "login.html"; // Redirect to login page
            } else {
                const error = await response.json();
                alert(`Failed to register: ${error.message}`);
            }
        } catch (error) {
            console.error("Error during registration:", error);
            alert("Something went wrong. Please try again later.");
        }
    });
});