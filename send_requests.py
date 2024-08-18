import requests
import json


def send_post_request(url, data):
    headers = {"Content-Type": "application/json"}
    response = requests.post(url, data=json.dumps(data), headers=headers, timeout=30)
    return response


# Predefined endpoint URL
endpoint_url = "http://localhost:8079/gamer/game_points"

# JSON data to send in the request body
json_data = {"game": "Left for Dead", "gamerId": "123", "points": 123}


for _ in range(10000):
    # Sending the POST request
    response = send_post_request(endpoint_url, json_data)

    # Printing the response
    print(response.status_code)
    print(response.text)
