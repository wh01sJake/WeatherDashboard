const BASE_API_URL = window.location.origin + "/api";

// DOM Elements
const welcomeMessage = document.getElementById("welcomeMessage");
const weatherContainer = document.getElementById("currentWeatherContent");
const favoritesList = document.getElementById("favoritesList");
const logoutButton = document.getElementById("logoutBtn");
const searchButton = document.getElementById("search");
const cityNameInput = document.getElementById("cityName");
const forecastContainer = document.getElementById("forecast-cards");
const addFavoriteBtn = document.getElementById("addFavoriteBtn");

let selectedCity = ""; // Track input city

// Add common fetch headers for reuse
function getAuthHeaders() {
    return {
        Authorization: `Bearer ${sessionStorage.getItem("token")}`,
        "Content-Type": "application/json",
    };
}

// Ensure the user is authenticated
document.addEventListener("DOMContentLoaded", async () => {
    const token = sessionStorage.getItem("token");

    if (!token) {
        redirectToLogin();
        return;
    }

    const isValid = await checkTokenValidity(token);
    if (isValid) {
        displayFavorites(); // Display the favorites list
    }
});

// Redirect to login if unauthorized
function redirectToLogin() {
    alert("You must be logged in to access the dashboard.");
    sessionStorage.clear();
    window.location.href = "login.html";
}

// Validate user's token
async function checkTokenValidity(token) {
    try {
        const response = await fetch(`${BASE_API_URL}/validate-token`, {
            method: "POST",
            headers: getAuthHeaders(),
        });

        if (response.ok) {
            const username = await response.text(); // Fetch username (example API response)
            welcomeMessage.innerHTML = `Welcome to your Dashboard, <strong>${username}</strong>!`;
            return true;
        }
        
        else {
            alert("Session expired. Redirecting to login...");
            redirectToLogin();
            return false;
        }
    } catch (error) {
        console.error("Error during token validation:", error);
        redirectToLogin();
        return false;
    }
}

// Handle search
searchButton.addEventListener("click", async () => {
    const city = cityNameInput.value.trim();
    if (!city) {
        alert("Please enter a city name!");
        return;
    }

    selectedCity = city; // Save the selected city
    await loadCurrentWeather();
    await loadForecast();
});

// Load current weather
async function loadCurrentWeather() {
    try {
        weatherContainer.innerHTML = `<p class="text-info">Loading weather data...</p>`;
        const response = await fetch(`${BASE_API_URL}/weather?city=${encodeURIComponent(selectedCity)}`, {
            headers: getAuthHeaders(),
        });

        if (response.ok) {
            const weatherData = await response.json();
            displayCurrentWeather(weatherData);
        } else {
            const error = await response.text();
            weatherContainer.innerHTML = `<p class="text-danger">Unable to load weather data: ${error}</p>`;
        }
    } catch (error) {
        console.error("Error loading weather data:", error);
        weatherContainer.innerHTML = `<p class="text-danger">Unable to load weather data. Try again later.</p>`;
    }
}

// Display current weather
function displayCurrentWeather(data) {
    weatherContainer.innerHTML = `
        <h5>${data.city}, ${data.country}</h5>
        <img src="https://openweathermap.org/img/wn/${data.icon}@2x.png" alt="${data.weather}">
        <p><strong>Temperature:</strong> ${data.temperature}°C</p>
        <p><strong>Condition:</strong> ${data.weather}</p>
        <p><strong>Humidity:</strong> ${data.humidity}%</p>
    `;
}

// Load 5-day weather forecast
async function loadForecast() {
    try {
        forecastContainer.innerHTML = `<p class="text-info">Loading forecast data...</p>`;
        const response = await fetch(`${BASE_API_URL}/weather/forecast?city=${encodeURIComponent(selectedCity)}`, {
            headers: getAuthHeaders(),
        });

        if (response.ok) {
            const forecastData = await response.json();
            displayForecast(forecastData);
        } else {
            const error = await response.text();
            forecastContainer.innerHTML = `<p class="text-danger">Unable to load forecast: ${error}</p>`;
        }
    } catch (error) {
        console.error("Error loading forecast:", error);
        forecastContainer.innerHTML = `<p class="text-danger">Unable to load forecast. Try again later.</p>`;
    }
}

// Display 5-day forecast
function displayForecast(data) {
    forecastContainer.innerHTML = ""; // Clear existing content
    data.forEach((forecast) => {
        forecastContainer.innerHTML += `
            <div class="col-md-4">
                <div class="card mb-3">
                    <div class="card-header">${new Date(forecast.date).toDateString()}</div>
                    <div class="card-body text-center">
                        <img src="https://openweathermap.org/img/wn/${forecast.icon}@2x.png" alt="${forecast.weather}">
                        <p><strong>${forecast.temperature}°C</strong></p>
                        <p>${forecast.weather}</p>
                    </div>
                </div>
            </div>
        `;
    });
}

// Add favorite city
addFavoriteBtn.addEventListener("click", async () => {
    if (!selectedCity) {
        alert("Please search for a city before adding to favorites!");
        return;
    }

    try {
        const response = await fetch(`${BASE_API_URL}/favorites?city=${encodeURIComponent(selectedCity)}`, {
            method: "POST",
            headers: getAuthHeaders(),
        });

        

        if (response.ok) {
            alert(`${selectedCity} has been added to your favorites!`);
            displayFavorites(); // Refresh favorites list
        } else {
            const error = await response.text();
            alert(`Failed to add city to favorites: ${error}`);
        }
    } catch (error) {
        console.error("Error adding favorite city:", error);
        alert("Unable to add city to favorites. Try again later.");
    }
});

// Display favorites
async function displayFavorites() {
    try {
        const response = await fetch(`${BASE_API_URL}/favorites`, {
            headers: getAuthHeaders(),
        });

        if (response.ok) {
            const favorites = await response.json();
            console.log("Favorites Response:", favorites); // Debug: Log the fetched favorites

            favoritesList.innerHTML = ""; // Clear the existing list
            if (favorites.length === 0) {
                favoritesList.innerHTML = `<li class="list-group-item">No favorites added yet.</li>`;
                return;
            }

            favorites.forEach((favorite) => {
                const listItem = document.createElement("li");
                listItem.className = "list-group-item d-flex justify-content-between align-items-center";

                // Correctly access 'name' (or another property based on backend response)
                listItem.textContent = favorite.name; 

                const removeBtn = document.createElement("button");
                removeBtn.className = "btn btn-danger btn-sm";
                removeBtn.textContent = "Remove";

                // Correctly pass the city name to the removeFavorite function
                removeBtn.addEventListener("click", () => {
                    const cityName = favorite.name; // Ensure  the correct property
                    console.log("Attempting to remove city:", cityName); // Debugging log
                    if (cityName) {
                        removeFavorite(cityName);
                    } else {
                        console.error("City name is undefined for this favorite:", favorite);
                        alert("Unable to remove city. Please try again.");
                    }
                });

                listItem.appendChild(removeBtn);
                favoritesList.appendChild(listItem);
            });
        } else {
            const error = await response.text();
            favoritesList.innerHTML = `<li class="list-group-item text-danger">Failed to load favorites: ${error}</li>`;
        }
    } catch (error) {
        console.error("Error fetching favorites:", error);
        favoritesList.innerHTML = `<li class="list-group-item text-danger">Unable to load favorites. Try again later.</li>`;
    }
}

// Remove favorite city
async function removeFavorite(city) {
    const confirmRemove = confirm(`Are you sure you want to remove "${city}" from your favorites?`);
    if (!confirmRemove) return;

    try {
        const response = await fetch(`${BASE_API_URL}/favorites?city=${encodeURIComponent(city)}`, {
            method: "DELETE",
            headers: getAuthHeaders(),
        });

        if (response.ok) {
            alert(`${city} has been removed from your favorites.`);
            displayFavorites(); // Refresh the favorites list
        } else {
            const error = await response.text();
            alert(`Failed to remove city from favorites: ${error}`);
        }
    } catch (error) {
        console.error("Error removing favorite city:", error);
        alert("Unable to remove city from favorites. Try again later.");
    }
}