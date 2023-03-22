# JMail 📩
A Java distributed email application.

## How it works
The application is divided in two modules:
- A mail server that manages user mailboxes and emails;
- A mail client.
<br /><br />

The user can use the mail client to:
- login
- read the emails in the inbox
- delete an email from the inbox
- compose a new email
- reply/reply all to an email
- forward an email

Users get notified whenever they receive a new email.

## Technical Details
### Server
The Server receives requests (Java Objects), passes it to the Request Handler which executes the methods that handle the specified operation (e.g. fetch updated user inbox), and sends back the response to the client.
<br />A fixed thread pool of 10 threads is used, and each request is handled by a thread in the pool; this optimizes the use of resources as it prevents the Server module from creating a brand new thread for each request.
<br /><br />
### Client
The Client, on the other side, constructs a request (creates a new Object) based on the action a user performed on the GUI, opens a network socket to communicate with the Server, and funnels the request through it. 
<br />Once the Client receives the response, it closes the network socket, and update the GUI accordingly.
<br /><br />The notification system consists of a scheduled thread that sends a request to the Server every second to check for new emails, displaying an alert if there are.
<br /><br />
### Data
Each email address is associated to a mailbox, implemented as a folder that has two main files (formatted in JSON):
- a file containing the inbox
- a file containing the emails that were sent by the user

## Libraries Used
- **JavaFX**: to implement the GUI
- **Gson**: to manage data in JSON

## Screenshots

![Screenshot 2023-03-20 at 22 03 36](https://user-images.githubusercontent.com/76702446/226465803-9b60a93b-4886-4cdb-a42c-a2212aec458f.png)
![Screenshot 2023-03-20 at 22 04 06](https://user-images.githubusercontent.com/76702446/226465825-6a167613-0082-463d-b022-e0905c99262d.png)
![Screenshot 2023-03-20 at 22 04 26](https://user-images.githubusercontent.com/76702446/226465843-a7508583-fcf1-4c41-8f03-95557df60684.png)
![Screenshot 2023-03-20 at 22 04 35](https://user-images.githubusercontent.com/76702446/226465861-9a894c75-92e0-46f6-846a-8d1d0cb78cba.png)
![Screenshot 2023-03-20 at 22 05 14](https://user-images.githubusercontent.com/76702446/226465878-21501d73-486d-4049-8ea6-5a9e112e0ddf.png)

## Demo
https://youtu.be/Tx_JI9DZntY
