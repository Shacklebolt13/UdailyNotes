package Giet.group12.udailynotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.storage.ListResult;

import java.util.List;

public class FileListToStringAdapter extends ArrayAdapter<ListResult>{


    public FileListToStringAdapter(@NonNull Context context, int resource, @NonNull List<ListResult> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.listitem,parent,false);
        }
        return convertView;
    }
}

