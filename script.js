document.getElementById("calculatorForm").addEventListener("submit", function(event) {
    // Prevent the default form submission behavior
    event.preventDefault();

    // Retrieve the values entered by the user
    const calculationType = document.getElementById("calculationType").value;
    const input1 = document.getElementById("input1").value;
    const input2 = document.getElementById("input2").value;
    // Add more input fields as needed
    
    // Construct the data object to be sent to the server
    const requestData = {
        calculationType: calculationType,
        input1: input1,
        input2: input2
        // Add more input fields as needed
    };

    // Send an HTTP POST request to the server with the data
    fetch("/calculate", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(requestData)
    })
    .then(response => {
        // Check if the response is successful
        if (!response.ok) {
            throw new Error("Network response was not ok");
        }
        // Parse the JSON response
        return response.json();
    })
    .then(data => {
        // Update the result div with the calculated result
        document.getElementById("result").innerText = data.result;
    })
    .catch(error => {
        // Log any errors that occur during the fetch operation
        console.error("Error:", error);
    });
});
