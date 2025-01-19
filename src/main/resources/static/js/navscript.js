
  // This will dynamically load the navbar from navbar.html
    document.addEventListener("DOMContentLoaded", () => {
        fetch("navbar.html")
            .then(response => response.text())
            .then(data => {
                document.querySelector("nav").outerHTML = data; // Load navbar
                setupNavbar(); // Setup dynamic login/logout functionality
            })
            .catch(error => console.error("Error loading navbar:", error));
    });

    // Setup Navbar Logic: Show login state dynamically
    function setupNavbar() {
        const token = sessionStorage.getItem("token");
        const loginStatus = document.getElementById("loginStatus");
        const logoutBtn = document.getElementById("logoutBtn");

        if (token) {
            loginStatus.textContent = "Logged in";
            logoutBtn.style.display = "inline";
            logoutBtn.addEventListener("click", () => {
                sessionStorage.removeItem("token");
                alert("Logged out successfully!");
                window.location.href = "login.html"; // Redirect upon logout
            });
        } else {
            loginStatus.textContent = "Not logged in";
            logoutBtn.style.display = "none";
        }
    }
