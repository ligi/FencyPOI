package org.battlehack.fencypoi;

import android.app.ListFragment;
import android.content.Context;
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
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by aschildbach on 6/8/13.
 */
public class PoiListFragment extends ListFragment {

    private POIDBCursorWrapper poi;
    private POIAdapter adapter;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        poi=new POIDBCursorWrapper(getActivity().managedQuery(POIDBContentProvider.CONTENT_URI, null, null, null, null));
        adapter = new POIAdapter(getActivity(), poi.getCursor(), true);

        setListAdapter(adapter);
    }

    private class POIAdapter extends CursorAdapter {

        public POIAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return LayoutInflater.from(context).inflate(R.layout.poi_list_entry, null);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView nameTextView=(TextView) view.findViewById(R.id.name);
            TextView typeTextView=(TextView) view.findViewById(R.id.type);

            nameTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_NAME)));
            typeTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_TYPE)));

            if (mActionMode!=null && cursor.getPosition()==mActionModeItemPosition) {
                view.setBackgroundResource(R.drawable.background_btn);
            } else {
                view.setBackground(null);
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container,false);
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

                mActionMode=getActivity().startActionMode(mActionModeCallback);
                adapter.notifyDataSetChanged();
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
            poi.setPosition(mActionModeItemPosition);
            switch (item.getItemId()) {
                case R.id.menu_share:
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q="+poi.getLatDbl()+","+poi.getLonDbl()+"("+poi.getName()+")"));
                    startActivity(Intent.createChooser(intent, "Share"));
                    mode.finish();
                    return true;
                case R.id.menu_delete:
                    String where = BaseColumns._ID + "='" + poi.getID()  + "'";
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
            adapter.notifyDataSetChanged();
        }
    };
}
