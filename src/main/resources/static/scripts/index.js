let data = [];
let usersCoords;
let latitude = null;
let longitude = null;

document.addEventListener('DOMContentLoaded', async () => {
    try {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(success, error);
        } else {
            console.log("Geolocation is not supported");
            await fetchData();
        }
    } catch (error) {
        console.error('Error fetching suggestions:', error);
    }
});

async function success(position) {
    usersCoords = [position.coords.latitude, position.coords.longitude];
    try {
        await fetchData();
    } catch (error) {
        console.error('Error fetching suggestions:', error);
    }
}

async function error() {
    console.log("Unable to retrieve your location");
    await fetchData();
}

async function fetchData() {
    try {
        let url = '/api/v1/interests/suggestions';

        // Append query parameters if latitude and longitude are provided
        if (latitude !== null && longitude !== null) {
            url += `?latitude=${latitude}&longitude=${longitude}`;
        }

        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}, Message: ${response.statusText}`);
        }

        const jsonData = await response.json();

        data = await Promise.all(jsonData.map(async (interest) => {
            interest.imageUrl = await fetchInterestImageUrl(interest);
            return interest;
        }));

        loadInterests();
    } catch (error) {
        console.error('Error fetching suggestions:', error);
    }
}

function loadInterests(query) {
    const container = document.querySelector('#suggestionList');
    container.innerHTML = '';
    const template = document.getElementsByTagName('template')[0];
    let dataSet = data;

    if (typeof query !== 'undefined' && !isEmptyOrSpaces(query)) {
        dataSet = data.filter(record => record.name.toLowerCase().includes(query.toLowerCase()));
    }

    dataSet.forEach(interest => {
        const clone = document.importNode(template.content, true);

        const elements = {
            interestLink: clone.querySelector('.discover-interest'),
            interestImg: clone.querySelector('.discover-interest img'),
            titleH3: clone.querySelector('.discover-interest-title h3'),
            distanceSpan: clone.querySelector('.discover-interest-distance span'),
            interestLikes: clone.querySelector('.discover-interest-like-value'),
            placesAmount: clone.querySelector('.discover-interest-places-amount'),
            interestDesc: clone.querySelector('.discover-interest-places p')
        };

        elements.interestLink.href = `/interests/${interest.id}`;
        elements.interestImg.src = interest.imageUrl;
        elements.titleH3.textContent = interest.name;

        if (usersCoords !== undefined) {
            elements.distanceSpan.textContent = distance(usersCoords[0], usersCoords[1], interest.latitude, interest.longitude).toFixed(1) + ' km';
        }

        elements.interestLikes.textContent = interest.likes;
        elements.placesAmount.textContent = interest.places;
        elements.interestDesc.textContent = interest.description;

        container.appendChild(clone);
    })
}

function createLike(iconClass, likeValue) {
    const li = document.createElement('li');

    const icon = document.createElement('i');
    icon.className = iconClass;
    li.appendChild(icon);

    const likeSpan = document.createElement('span');
    likeSpan.className = 'discover-interest-likes';
    likeSpan.textContent = likeValue;
    li.appendChild(likeSpan);

    return li;
}

function isEmptyOrSpaces(str) {
    return str === null || str.match(/^ *$/) !== null;
}

function distance(lat1, lon1, lat2, lon2) {
    let radlat1 = Math.PI * lat1 / 180;
    let radlat2 = Math.PI * lat2 / 180;
    let theta = lon1 - lon2;
    let radtheta = Math.PI * theta / 180;
    let dist = Math.sin(radlat1) * Math.sin(radlat2) + Math.cos(radlat1) * Math.cos(radlat2) * Math.cos(radtheta);
    if (dist > 1) {
        dist = 1;
    }
    dist = Math.acos(dist);
    dist = dist * 180 / Math.PI;
    dist = dist * 60 * 1.85316;
    return dist;
}
