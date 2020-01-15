package com.example.ubuestelas.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.ubuestelas.R;
import com.google.android.gms.common.util.ArrayUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CharacterSelectionAdapter extends BaseAdapter {
    Context context;

    public Integer[] characters;

    public CharacterSelectionAdapter(Context context){
        this.context = context;
        characters = getCharacters();
    }

    public CharacterSelectionAdapter(Context context, int position){
        this.context=context;
        characters = getCharactersWithSelected(position);
    }
    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return characters.length;
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return characters[position];
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(characters[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setLayoutParams(new GridView.LayoutParams(240,240));
        return imageView;
    }

    public Integer[] getCharacters(){
        Integer[] characs = {};
        JSONObject obj;
        try {
            obj = new JSONObject(Util.loadJSONFromAsset(context, "characters.json"));
            JSONArray chars = obj.getJSONArray("characters");
            for (int i = 0; i<chars.length(); i++){
                JSONObject charac = chars.getJSONObject(i);
                int resourceId = context.getResources().getIdentifier(charac.getString("characterImage"), "drawable", context.getPackageName());
                characs = ArrayUtils.appendToArray(characs, resourceId);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return characs;
    }

    public Integer[] getCharactersWithSelected(int position){
        Integer[] characs = {};
        JSONObject obj;
        try {
            obj = new JSONObject(Util.loadJSONFromAsset(context, "characters.json"));
            JSONArray chars = obj.getJSONArray("characters");
            for (int i = 0; i<chars.length(); i++){
                JSONObject charac = chars.getJSONObject(i);
                if(i==position){
                    int resourceId = context.getResources().getIdentifier(charac.getString("whenSelected"), "drawable", context.getPackageName());
                    characs = ArrayUtils.appendToArray(characs, resourceId);
                }else {
                    int resourceId = context.getResources().getIdentifier(charac.getString("characterImage"), "drawable", context.getPackageName());
                    characs = ArrayUtils.appendToArray(characs, resourceId);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return characs;
    }
}
