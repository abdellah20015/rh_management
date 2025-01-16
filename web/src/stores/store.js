import { defineStore } from "pinia";
import utils from "@/shared/utils";
import services from "@/shared/services";

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,    
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

    // Vérification de l'authentification
    async checkauth() {
      try {
        const data = await utils.fetch_methode(services.checkauth);
        if (data && data.userData) {
          this.user = data.userData;
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
