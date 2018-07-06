package com.example.jona.latacungadigital.Activities.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jona.latacungadigital.Activities.modelos.CharacterModel;
import com.example.jona.latacungadigital.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CharacterAdapter extends RecyclerView.Adapter {

    private List<CharacterModel> characterModelList;
    private int mSelectedItem = -1, poisitionList;
    private String keyCharacter;

    public CharacterAdapter(List<CharacterModel> characterModelList) {
        this.characterModelList = characterModelList;
    }

    public String getKeyCharacter() { return keyCharacter; }

    public int getPoisitionList() { return poisitionList; }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.character_item_view, parent, false);
        return new CharacterHolder(view, true);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CharacterModel characterModel = characterModelList.get(position);
        ((CharacterHolder) holder).getRadioButton().setChecked(position == mSelectedItem);
        ((CharacterHolder) holder).bind(characterModel, position);
    }

    @Override
    public int getItemCount() { return characterModelList.size(); }

    public class CharacterHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView txtNameCharacter;
        private CircleImageView circleImageView;
        private RadioButton radioButton;
        private LinearLayout linearLayout;
        private boolean isSelectedFirstTime;

        private CharacterHolder(View itemView, boolean isSelectedFirstTime) {
            super(itemView);

            txtNameCharacter = itemView.findViewById(R.id.txtNameCharacter);
            circleImageView = itemView.findViewById(R.id.characterImage);
            radioButton = itemView.findViewById(R.id.radioButtonCharacters);
            linearLayout = itemView.findViewById(R.id.linearLayoutCharacter);
            this.view = itemView;
            this.isSelectedFirstTime = isSelectedFirstTime;
        }

        private RadioButton getRadioButton() { return radioButton; }

        public void bind(final CharacterModel character, final int position) {

            txtNameCharacter.setText(character.getNameCharacter());

            if (isSelectedFirstTime) {
                radioButton.setChecked(characterModelList.get(position).isSelected());
            }

            Glide.with(view.getContext()).load(character.getImageURL()).crossFade().centerCrop().into(circleImageView);

            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectedItem = position;
                    notifyItemRangeChanged(0, characterModelList.size());
                }
            };

            poisitionList = mSelectedItem; // Para no guardar la posicion -1.

            radioButton.setOnClickListener(clickListener);
            linearLayout.setOnClickListener(clickListener);

            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        keyCharacter = characterModelList.get(position).getKeyCharacter();
                    }
                }
            });

            isSelectedFirstTime = false;
        }
    }
}
