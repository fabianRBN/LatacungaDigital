package com.example.jona.latacungadigital.Activities.modelos;

public class CharacterModel {

    private String nameCharacter;
    private boolean isSelected;
    private String imageURL;
    private String keyCharacter;

    public CharacterModel() { }

    public String getNameCharacter() { return nameCharacter; }

    public void setNameCharacter(String nameCharacter) { this.nameCharacter = nameCharacter; }

    public boolean isSelected() { return isSelected; }

    public void setSelected(boolean selected) { isSelected = selected; }

    public String getImageURL() { return imageURL; }

    public void setImageURL(String imageURL) { this.imageURL = imageURL; }

    public String getKeyCharacter() { return keyCharacter; }

    public void setKeyCharacter(String keyCharacter) { this.keyCharacter = keyCharacter; }
}
