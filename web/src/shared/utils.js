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

export default fetch_methode;
