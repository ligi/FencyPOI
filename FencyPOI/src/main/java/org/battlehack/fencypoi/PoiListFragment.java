package org.battlehack.fencypoi;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;

/**
 * Created by aschildbach on 6/8/13.
 */
public class PoiListFragment extends ListFragment {

    private Cursor cursor;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cursor = getActivity().managedQuery(POIDBContentProvider.CONTENT_URI, null, null, null, null);
        setListAdapter(new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_2, cursor, new String[]{POIDBContentProvider.KEY_NAME, POIDBContentProvider.KEY_DESCRIPTION}, new int[]{android.R.id.text1, android.R.id.text2}));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list, null);
    }

    @Override
    public void onResume() {

        super.onResume();

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                cursor.move(i);

                String where = BaseColumns._ID + "='" + cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID)) + "'";
                getActivity().getContentResolver().delete(POIDBContentProvider.CONTENT_URI, where,null );
                return false;
            }
        });
        getListView().setEmptyView(getView().findViewById(android.R.id.empty));
    }
}
