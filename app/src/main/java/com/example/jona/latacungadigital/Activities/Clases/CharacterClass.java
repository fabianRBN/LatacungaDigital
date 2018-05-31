package com.example.jona.latacungadigital.Activities.Clases;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CharacterClass {

    // Se crea una interface para guardar el nombre y la imagen del personaje consultado.
    public interface DataOfCharacters {
        void nameCharacter(String nameCharacter, String imageCharacterURL);
    }

    // Constructor.
    public CharacterClass() {}

    // Método para leer de la base de datos de Firebase los datos del personaje seleccionado por el usuario.
    public void ReadCharacterFromDatabase(final DataOfCharacters dataOfCharacters) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("personaje");

        // Creamos el query de la consulta de la base de datos.
        databaseReference.orderByChild("seleccionado").equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot allDataCharacter: dataSnapshot.getChildren()) {

                    for (DataSnapshot dataImageURL: allDataCharacter.child("galeria").getChildren()) {
                        // Guardamos el nombre y la direccion de la imagen en una interface para poder recuperar la informacion en otra clase.
                        dataOfCharacters.nameCharacter(allDataCharacter.child("nombre").getValue(String.class),
                                dataImageURL.child("imagenURL").getValue(String.class));
                    }
                }

            }

            // Método en caso de que exista un error en la consulta.
            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.getMessage();
            }
        });
    }

}
