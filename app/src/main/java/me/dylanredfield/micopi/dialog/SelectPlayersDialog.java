package me.dylanredfield.micopi.dialog;

import java.util.ArrayList;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.parse.ParseObject;

import me.dylanredfield.micopi.R;
import me.dylanredfield.micopi.fragment.NewGameFragment;
import me.dylanredfield.micopi.util.Helpers;
import me.dylanredfield.micopi.util.Keys;


public class SelectPlayersDialog extends AbstractListViewDialog {
    private ArrayList<ParseObject> mSelectedPlayersList = new ArrayList<>();
    private TextView mAdd;

    @Override
    public void setListeners() {
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (view.getTag() == null) {
                    view.setTag(true);
                    mSelectedPlayersList.add(getList().get(i));
                    ((TextView) view.findViewById(R.id.lang)).setTextColor(getResources()
                            .getColor(R.color.text_light_grey));
                } else {
                    if ((Boolean) view.getTag()) {
                        mSelectedPlayersList.remove(i);
                        ((TextView) view.findViewById(R.id.lang)).setTextColor(getResources()
                                .getColor(R.color.lang_pink));
                        view.setTag(false);
                    } else {
                        mSelectedPlayersList.add(getList().get(i));
                        ((TextView) view.findViewById(R.id.lang)).setTextColor(getResources()
                                .getColor(R.color.text_light_grey));
                        view.setTag(true);
                    }
                }
                if (mSelectedPlayersList.size() > 0) {
                    ((TextView) getDialog().findViewById(R.id.add)).setTypeface(getFont());
                    getAdd().setVisibility(View.VISIBLE);
                } else {

                    getAdd().setVisibility(View.GONE);
                }
            }
        });

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseObject newGame = ((NewGameFragment) getTargetFragment()).getNewGame();
                newGame.put(Keys.INVITED_PLAYERS_ARR, mSelectedPlayersList);
                ((NewGameFragment) getTargetFragment()).getSelectPlayers()
                        .setText(Html.fromHtml(
                                Helpers.getHtmlString("game", "" + getResources()
                                        .getColor(R.color.text_orange)) + ".players = " +
                                        getColoredToString(Helpers.getStringArrayFromPoint(
                                                mSelectedPlayersList)) + ";"));
                ((NewGameFragment) getTargetFragment()).getLayout3().setVisibility(View.VISIBLE);

                dismiss();
            }
        });
    }

    @Override
    public void setDefaultValues() {
        getLabel().setText("//AddPlayers");
        mAdd = getAdd();
    }

    private String getColoredToString(ArrayList<String> list) {
        String returnString = "[";
        for (int i = 0; i < list.size(); i++) {
            returnString += Helpers.getHtmlString(list.get(i), "" +
                    getResources().getColor(R.color.player_list_blue));

            if (i != list.size() - 1) {
                returnString += ",";
            } else {
                returnString += "]";
            }
        }
        return returnString;
    }

    public static SelectPlayersDialog newInstance() {
        SelectPlayersDialog dialog = new SelectPlayersDialog();
        return dialog;
    }
}
