package org.battlehack.fencypoi;

import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
                if (mActionMode != null) {
                    return true;
                }
                mActionModeItemPosition = i;
                getActivity().startActionMode(mActionModeCallback);
                return true;
            }
        });
        getListView().setEmptyView(getView().findViewById(android.R.id.empty));
    }


    private ActionMode mActionMode;
    private int mActionModeItemPosition;

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.list_action_mode, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_share:
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:42,24"));
                    startActivity(Intent.createChooser(intent, "Share"));
                    mode.finish();
                    return true;
                case R.id.menu_delete:
                    cursor.move(mActionModeItemPosition);
                    String where = BaseColumns._ID + "='" + cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID)) + "'";
                    getActivity().getContentResolver().delete(POIDBContentProvider.CONTENT_URI, where, null);
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }


        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };
}
