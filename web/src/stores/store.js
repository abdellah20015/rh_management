import { defineStore } from "pinia";
import fetch_methode from "@/shared/utils";
import services from "@/shared/services";

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,    
    user_id: null,
    initialized: false,
  }),
  actions: {
    // Authentification avec un utilisateur
    auth(data) {
      this.user = data.user;
      this.initialized = true;
      console.log(this.user)
    },

    // Déconnexion
    logout() {
      this.user = null;   
      this.initialized = false;
    },
    // user_id 
    setUserId(id) {
      console.log("from store  :", id); 
      this.user_id = id; 
    },

    // Vérification de l'authentification
    async checkauth() {
      try {
        const data = await fetch_methode(services.checkauth);
        if (data.ok) {
          this.user =  await data.json();
          this.initialized = true;
        } else {
          this.user = null;
        }
      } catch (e) {
        console.log(e);
      }
    }
  }
});
