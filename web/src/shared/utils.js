import index from './index';

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


export default {
    fetch_methode,
    convertDate,
    formatString

};
