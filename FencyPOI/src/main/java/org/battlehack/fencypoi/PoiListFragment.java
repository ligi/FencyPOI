package org.battlehack.fencypoi;

import android.R;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

/**
 * Created by aschildbach on 6/8/13.
 */
public class PoiListFragment extends ListFragment {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Cursor cursor = getActivity().managedQuery(POIDBContentProvider.CONTENT_URI, null, null, null, null);

        setListAdapter(new SimpleCursorAdapter(getActivity(), R.layout.simple_list_item_2, cursor, new String[]{POIDBContentProvider.KEY_NAME, POIDBContentProvider.KEY_DESCRIPTION}, new int[]{android.R.id.text1, android.R.id.text2}));
    }
}
