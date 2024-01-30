const defaultView = [49.8037633, 15.4749126];
const defaultPlaceZoom = 14;
const defaultZoom = 7;
const mapUrl = 'https://tile.openstreetmap.org/{z}/{x}/{y}.png';
const tileLayerOptions = {
    maxZoom: 19,
    attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
};

let placeLatLng;
let map;

if (placeLat && placeLng) {
    placeLatLng = L.latLng([placeLat, placeLng]);
    map = L.map('map').setView(placeLatLng, defaultPlaceZoom);
} else {
    placeLatLng = L.latLng(defaultView);
    map = L.map('map').setView(placeLatLng, defaultZoom);
}

L.tileLayer(mapUrl, tileLayerOptions).addTo(map);

let marker;
let inputLat = document.getElementById("place-latitude");
let inputLng = document.getElementById("place-longitude");

if (placeLat && placeLng) {
    createMarker(placeLat, placeLng);
}

map.on("click", async function (e) {
    try {
        let lat = e.latlng.lat;
        let lng = e.latlng.lng;
        createMarker(lat, lng);
        // Wait for findAddress to complete before moving on
        await handleAddressSearch(lat, lng);
    } catch (error) {
        console.error('Error handling map click:', error);
    }
});

map.on('locationfound', onLocationFound);
map.on('locationerror', onLocationError);

function deleteAddress() {
    document.getElementById('address').value = '';
}
function createMarker(lat, lng) {
    if (marker) {
        marker.remove();
    }
    fillInputFields(lat, lng);
    marker = new L.marker([lat, lng], {draggable: true, autoPan: true}).addTo(map);
    marker.on("dragend", async function (event) {
        const updatedLat = event.target.getLatLng().lat;
        const updatedLng = event.target.getLatLng().lng;
        fillInputFields(updatedLat, updatedLng);
        try {
            await handleAddressSearch(updatedLat, updatedLng);
        } catch (error) {
            console.error('Error handling map click:', error);
        }
    });
}

function fillInputFields(lat, lng) {
    inputLat.value = lat.toFixed(6);
    inputLng.value = lng.toFixed(6);
}

function locate() {
    map.locate({setView: true, maxZoom: 16});
}

async function onLocationFound(e) {
    const lat = e.latlng.lat;
    const lng = e.latlng.lng;
    createMarker(lat, lng);
    try {
        await handleAddressSearch(lat, lng);
    } catch (error) {
        console.error('Error handling map click:', error);
    }
}

function onLocationError(e) {
    alert(e.message);
}

const RATE_LIMIT = 2000; // 2 seconds
let lastRequestTime = 0;

async function handleAddressSearch(lat, lng) {
    try {
        const address = await findAddress(lat, lng);
        if (address) {
            document.getElementById("address").value = address;
        }
    } catch (error) {
        console.error('Error handling address search:', error);
    }
}
async function findAddress(lat, lng) {
    const apiUrl = `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}`;
    const result = await makeApiRequest(apiUrl, 'Error fetching address:');

    if (result) {
        // Consider returning the result instead of directly updating the DOM
        return result.display_name;
    } else {
        console.log('No results found');
        return null;
    }
}

async function handleCoordinatesSearch() {
    try {
        const result = await findCoordinates();
        if (result) {
            const { lat, lng, displayName } = result;
            map.setView([lat, lng], defaultPlaceZoom);
            createMarker(lat, lng);
            document.getElementById('address').value = displayName;
        }
    } catch (error) {
        console.error('Error handling coordinates search:', error);
    }
}

async function findCoordinates() {
    const addressInput = document.getElementById('address');
    const address = addressInput.value;
    const apiUrl = `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(address)}`;
    const result = await makeApiRequest(apiUrl, 'Error fetching coordinates:');

    if (result && result.length > 0) {
        const lat = Number(result[0].lat);
        const lng = Number(result[0].lon);
        const displayName = result[0].display_name;

        // Consider returning values instead of directly updating the DOM
        return { lat, lng, displayName };
    } else {
        console.log('No results found');
        return null;
    }
}

async function delayIfNeeded() {
    const currentTime = Date.now();
    const timeSinceLastRequest = currentTime - lastRequestTime;

    if (timeSinceLastRequest < RATE_LIMIT) {
        const delay = RATE_LIMIT - timeSinceLastRequest;
        await new Promise(resolve => setTimeout(resolve, delay));
    }
    lastRequestTime = Date.now();
}

async function makeApiRequest(apiUrl, errorMessage) {
    try {
        await delayIfNeeded();

        const response = await fetch(apiUrl);

        if (!response.ok) {
            throw new Error(`Failed to fetch data from the Nominatim API. Status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error(errorMessage, error);
        return null;
    }
}
