window.websocket = new WebSocket("ws://localhost:4444");
function getSensorNumber(){
    window.websocket.send('Sensors: '.concat(document.getElementById("sensorNumberField").value))
}

function sendFrequency(){
window.websocket.send('Frequency: '.concat(document.getElementById("frequencyField").value))
}