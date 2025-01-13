package com.example.rh.constants;

public class Demands {

  // Template for Attestation de Travail
  public static final String ATTESTATION_TRAVAIL = """
        Je soussigné(e), [manager_name], agissant en qualité de [manager_role], certifie que :

        Nom d’utilisateur : [username]
        Est employé(e) au sein de notre entreprise sous contrat de type [contract_type] depuis le [start_date].

       Cette attestation est délivrée à la demande de l'intéressé(e) pour servir et valoir ce que de droit.
    """;
}
