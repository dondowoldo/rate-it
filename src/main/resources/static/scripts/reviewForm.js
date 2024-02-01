const TIMEOUT_DURATION = 2000;
document.addEventListener('DOMContentLoaded', async function () {
    updateCharCountAndResize(document.querySelector("#review-text-field").getAttribute("maxlength"));

    const rateButton = document.querySelector("#rate");
    rateButton.setAttribute('data-bs-toggle', 'modal');
    rateButton.setAttribute('data-bs-target', '#review-modal');

    const reviewTextField = document.querySelector("#review-text-field");
    reviewTextField.addEventListener('input', function () {
        updateCharCountAndResize(reviewTextField.getAttribute("maxlength"));
    });

    if (review) {
        displayReview(review);
    } else {
        displayReview();
    }

    async function handleSubmit(event) {
        try {
            event.preventDefault();

            const reviewText = reviewTextField.value;

            if (!reviewText.trim()) {
                displayMessage('Please enter a new review before submitting.', 'secondary');
                return;
            }

            const response = await fetch(`/api/v1/places/${placeId}/review`, {
                method: 'POST',
                body: reviewText,
                headers: {
                    'Content-Type': 'text/plain'
                },
            });

            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }

            const newReview = await response.json(); // Assuming the server returns the new review object

            // Display the new review instantly
            displayReview(newReview);

            displayMessage('Review sent successfully!', 'success');

            rateButton.removeAttribute('data-bs-toggle');
            rateButton.removeAttribute('data-bs-target');

            setTimeout(function () {
                $('#review-modal').modal('hide');
            }, TIMEOUT_DURATION);

            previousReviewText = reviewText;

        } catch (error) {
            console.error(error);

            displayMessage('Error sending review.', 'danger');
        }
    }

    const form = document.getElementById("review-form");
    form.addEventListener('submit', handleSubmit);
});

function displayReview(newReview) {
    const reviewContainer = document.querySelector('.place-review');
    const reviewElement = reviewContainer.querySelector('.review-container');
    const reviewTextElement = reviewElement.querySelector('h2');
    const reviewContentElement = reviewElement.querySelector('p');

    // Check if timestamp and buttons already exist
    let timestampElement = reviewElement.querySelector('.timestamp');
    let editButton = reviewElement.querySelector('.btn-primary');
    let deleteButton = reviewElement.querySelector('.btn-danger');

    // Update the review elements based on new data or reset to default values
    reviewTextElement.textContent = newReview ? 'Your Review' : 'Review';
    reviewContentElement.textContent = newReview ? newReview.text : 'Rate to add your review!';

    if (newReview) {
        // If timestamp doesn't exist, create it
        if (!timestampElement) {
            timestampElement = document.createElement('p');
            timestampElement.classList.add('timestamp');
            reviewElement.appendChild(timestampElement);
        }

        // Update the timestamp
        timestampElement.textContent = new Date(newReview.timestamp).toLocaleString();

        // If edit button doesn't exist, create it
        if (!editButton) {
            editButton = document.createElement('button');
            editButton.textContent = 'Edit Review';
            editButton.classList.add('btn', 'btn-primary', 'mr-2');
            editButton.addEventListener('click', function () {
                // Open the review modal or perform any desired edit action
                $('#review-modal').modal('show');
            });
            reviewElement.appendChild(editButton);
        }

        // If delete button doesn't exist, create it
        if (!deleteButton) {
            deleteButton = document.createElement('button');
            deleteButton.textContent = 'Delete Review';
            deleteButton.classList.add('btn', 'btn-danger');
            deleteButton.addEventListener('click', function () {
                // Call the specified REST endpoint for deleting the review
                deleteReview(); // You can define the deleteReview function as needed
            });
            reviewElement.appendChild(deleteButton);
        }
    } else {
        // If there's no review, remove additional elements
        if (timestampElement) {
            reviewElement.removeChild(timestampElement);
        }

        if (editButton) {
            reviewElement.removeChild(editButton);
        }

        if (deleteButton) {
            reviewElement.removeChild(deleteButton);
        }
    }
}



// Function to handle deleting the review (replace with your actual endpoint)
// Function to handle deleting the review
async function deleteReview() {
    try {
        const response = await fetch(`/api/v1/places/${placeId}/delete-review`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
    } catch (error) {
        console.error(error);
    }
}

function displayMessage(message, type) {
    const charCountElement = document.querySelector('#charCount');

    charCountElement.innerHTML = `<div class="alert alert-${type}" role="alert">${message}</div>`;

    setTimeout(function () {
        charCountElement.innerHTML = '';
    }, TIMEOUT_DURATION);
}

function updateCharCountAndResize(maxLength) {
    const textarea = document.querySelector("#review-text-field");
    const charCount = document.querySelector("#charCount");
    const remainingChars = maxLength - textarea.value.length;
    charCount.textContent = "Characters remaining: " + remainingChars;
    textarea.style.height = textarea.scrollHeight + "px";
}