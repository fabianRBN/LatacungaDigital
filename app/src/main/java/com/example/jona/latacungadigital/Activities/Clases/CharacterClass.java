package com.example.jona.latacungadigital.Activities.Clases;

import com.example.jona.latacungadigital.Activities.modelos.CharacterModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CharacterClass {

    // Se crea una interface para guardar el nombre y la imagen del personaje consultado.
    public interface DataOfCharacters {
        void dataCharacterSelected(String nameCharacter, String imageCharacterURL);
    }

    public interface AllCharacters {
        void dataCharacters(List<CharacterModel> characterModelList);
    }

    public interface KeyLastChracter {
        void keyLastCharacter(String keyCharacter);
    }

    public interface GenreCharacter {
        void genreCharacter(String genreCharacter);
    }

    public interface GenreCharacterFromChanged {
        void genreCharacterFromChanged(String genreCharacter);
    }

    // Constructor.
    public CharacterClass() {}

    // Método para leer de la base de datos de Firebase los datos del personaje seleccionado por el usuario.
    public void ReadCharacterFromDatabase(final DataOfCharacters dataOfCharacters) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("cliente");

        databaseReference.orderByChild("idcliente").equalTo(getCurrentUserSignedUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String key = "";
                for (DataSnapshot allDataCharacter: dataSnapshot.getChildren()) {
                    // Guardamos la clave del personaje seleccionado en una varibale.
                    key = allDataCharacter.child("personajeID").getValue(String.class);
                }

                // Se crea una nueva consulta para retornar los datos del personaje
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = database.getReference("personaje");

                final String KeyCharacter = key;

                // Creamos el query de la consulta de la base de datos.
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot allDataCharacter: dataSnapshot.getChildren()) {

                            if (allDataCharacter.getKey().equals(KeyCharacter)) {
                                for (DataSnapshot dataImageURL: allDataCharacter.child("galeria").getChildren()) {
                                    // Guardamos el nombre y la direccion de la imagen en una interface para poder recuperar la informacion en otra clase.
                                    dataOfCharacters.dataCharacterSelected(allDataCharacter.child("nombre").getValue(String.class),
                                            dataImageURL.child("imagenURL").getValue(String.class));
                                }
                            }
                        }
                    }

                    // Método en caso de que exista un error en la consulta.
                    @Override
                    public void onCancelled(DatabaseError databaseError) { databaseError.getMessage(); }
                });
            }

            // Método en caso de que exista un error en la consulta.
            @Override
            public void onCancelled(DatabaseError databaseError) { databaseError.getMessage(); }
        });
    }

    // Método para obtener una lista de todos los personajes registrados.
    public void getAllCharacterFromDataBase(final AllCharacters allCharacters) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("cliente");

        databaseReference.orderByChild("idcliente").equalTo(getCurrentUserSignedUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String key = "";
                for (DataSnapshot allDataCharacter: dataSnapshot.getChildren()) {

                    // Guardamos la clave del personaje seleccionado en una varibale.
                    key = allDataCharacter.child("personajeID").getValue(String.class);
                }

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = database.getReference("personaje");

                final String KeyCharacter = key;

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        List<CharacterModel> characterModelList = new ArrayList<>(); // Lista de toda la información de los Personajes.

                        // Proceso para llenar la lista con toda la informacion del personaje.
                        for (DataSnapshot allDataCharacter: dataSnapshot.getChildren()) {

                            for (DataSnapshot dataImageURL: allDataCharacter.child("galeria").getChildren()) {
                                // Creamos una nueva clase de tipo Personajes para guardar sus respectivos datos.
                                CharacterModel characterModel = new CharacterModel();
                                characterModel.setKeyCharacter(allDataCharacter.getKey()); // Valor de la clave del personaje.
                                characterModel.setNameCharacter(allDataCharacter.child("nombre").getValue(String.class));
                                characterModel.setImageURL(dataImageURL.child("imagenURL").getValue(String.class));

                                if (allDataCharacter.getKey().equals(KeyCharacter)) { // Añadimos si esta seleccionado por el usuario.
                                    characterModel.setSelected(true);
                                } else {
                                    characterModel.setSelected(false);
                                }

                                characterModelList.add(characterModel); // Añadimos a la lista a todos los personajes que estan en la base de datos.
                                allCharacters.dataCharacters(characterModelList); // Guardamos la información de este en una Lista de Personajes.
                            }
                        }
                    }

                    // Método en caso de que exista un error en la consulta.
                    @Override
                    public void onCancelled(DatabaseError databaseError) { databaseError.getMessage(); }
                });

            }

            // Método en caso de que exista un error en la consulta.
            @Override
            public void onCancelled(DatabaseError databaseError) { databaseError.getMessage(); }
        });
    }

    public void updateKeyCharacterSeletedFormUser(final String keyCharacter) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("cliente");

        databaseReference.orderByChild("idcliente").equalTo(getCurrentUserSignedUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Actualizamos la clave del personaje de acuerdo a lo que haya escogido el usuario.
                for (DataSnapshot allDataCharacter: dataSnapshot.getChildren()) {
                    allDataCharacter.getRef().child("personajeID").setValue(keyCharacter);
                }

            }

            // Método en caso de que exista un error en la consulta.
            @Override
            public void onCancelled(DatabaseError databaseError) { databaseError.getMessage(); }
        });
    }

    // Método para obtener la ultima clave del Personaje.
    public void getKeyLastCharacter(final KeyLastChracter keyLastChracter) {
        // Se crea una nueva consulta para retornar los datos del personaje
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("personaje");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String keyCharacter = "";
                for (DataSnapshot allDataCharacter: dataSnapshot.getChildren()) {
                    keyCharacter = allDataCharacter.getKey();
                }

                keyLastChracter.keyLastCharacter(keyCharacter); // Se guarda la clave del ultimo personaje.

            }

            @Override
            public void onCancelled(DatabaseError databaseError) { databaseError.getMessage(); }
        });
    }

    // Método para obtener el genero del personaje seleccionado por el usuario.
    public void getGenreCharacter(final GenreCharacter genreCharacter) {
        // Se crea una nueva consulta para retornar los datos del personaje
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("cliente");

        databaseReference.orderByChild("idcliente").equalTo(getCurrentUserSignedUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String key = "";
                for (DataSnapshot allDataCharacter: dataSnapshot.getChildren()) {
                    // Guardamos la clave del personaje seleccionado en una varibale.
                    key = allDataCharacter.child("personajeID").getValue(String.class);
                }

                // Se crea una nueva consulta para retornar los datos del personaje
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = database.getReference("personaje");

                final String KeyCharacter = key;

                // Creamos el query de la consulta de la base de datos.
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot allDataCharacter: dataSnapshot.getChildren()) {

                            if (allDataCharacter.getKey().equals(KeyCharacter)) {
                                genreCharacter.genreCharacter(allDataCharacter.child("sexo").getValue(String.class));
                            }
                        }
                    }

                    // Método en caso de que exista un error en la consulta.
                    @Override
                    public void onCancelled(DatabaseError databaseError) { databaseError.getMessage(); }
                });
            }

            // Método en caso de que exista un error en la consulta.
            @Override
            public void onCancelled(DatabaseError databaseError) { databaseError.getMessage(); }
        });
    }

    // Método para obtener el género del personaje cuando el usuario cambie este.
    public void getGenreCharacterFormChanged(final String keyCharacter, final GenreCharacterFromChanged genreCharacterFromChanged) {
        // Se crea una nueva consulta para retornar los datos del personaje
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("personaje");

        // Creamos el query de la consulta de la base de datos.
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot allDataCharacter: dataSnapshot.getChildren()) {

                    if (allDataCharacter.getKey().equals(keyCharacter)) {
                        genreCharacterFromChanged.genreCharacterFromChanged(allDataCharacter.child("sexo").getValue(String.class));
                    }
                }
            }

            // Método en caso de que exista un error en la consulta.
            @Override
            public void onCancelled(DatabaseError databaseError) { databaseError.getMessage(); }
        });
    }

    // Método para obtener el uid del usuario Logeado de la aplicación.
    private String getCurrentUserSignedUID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = "";

        if (user != null) { // Si el usuario esta logeado.
            uid = user.getUid(); // Obtenemos el uid del usuario logeado.
        }

        return uid; // Retornamos el uid del usuario.
    }
}
