package edu.fandm.teamyellowstone.wordly;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class GraphViewAdapter extends ArrayAdapter<String> {

    public GraphViewAdapter(@NonNull Context context, ArrayList<String> arrayList) {
        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentItemView = convertView;
        if (currentItemView == null) {
            currentItemView = View.inflate(getContext(), R.layout.graph_item, null);
        }
        TextView text = currentItemView.findViewById(R.id.itemET);
        if(position ==getCount()-1 || position == 0){
            text.setText(getItem(position));
        }

        return currentItemView;

    }

}
