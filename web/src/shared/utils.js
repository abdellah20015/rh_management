import index from './index';
import Swal from 'sweetalert2';
import 'sweetalert2/dist/sweetalert2.min.css';
const fetch_methode = async (url, body = null, headers = {}) => {
    try {
        url = index.server_adress + url;
        if (body && typeof body === "object") {
            headers['Content-Type'] = 'application/json';
            body = JSON.stringify(body);
        }
        const response = await fetch(url, {
            method: "POST",
            headers,
            body,
            credentials: "include"
        });
        return response
        // if (!response.ok) {
        //     const errorData = await response.json();
        //     throw new Error(errorData);
        // }
        // return await response.json();
    } catch (error) {
        console.error('Erreur lors de la requête :', error);
        throw error;
    }
};


const convertDate = (date) => {
    const formattedDate = new Date(date).toISOString().split("T")[0];
    return formattedDate;
};


//convert string from ex: demande_de_conge to Demande de conde
const formatString = (str) => {
    return str
        .replace(/_/g, ' ') // Replace underscores with spaces
        .toLowerCase()      // Convert to lowercase
        .replace(/(^\w|\s\w)/g, (match) => match.toUpperCase()); // Capitalize words
}

// Method to display a success alert
const successAlert = (message) => {
    Swal.fire({
        title: 'Succès!',
        text: message,
        icon: 'success',
        toast: true,
        position: 'top-end',
        showConfirmButton: false,
        timer: 3000,
        timerProgressBar: true,
        customClass: {
            popup: 'small-toast'
        }
    });
};
// Method to display an error alert
const errorAlert = (message) => {
    Swal.fire({
        title: 'Erreur!',
        text: message,
        icon: 'error',
        toast: true,
        position: 'top-end',
        showConfirmButton: false,
        timer: 3000,
        timerProgressBar: true,
        customClass: {
            popup: 'small-toast'
        }
    });
};


export default {
    fetch_methode,
    convertDate,
    formatString,
    successAlert,
    errorAlert

};
