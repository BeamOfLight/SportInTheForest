package beamoflight.sportintheforest;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

public class UsersActivity extends Activity {

    DBHelper dbHelper;
    GameHelper gameHelper;
    List<Map<String, String>> usersData;

    ListView lvUsers, lvNewUser;

    AlertDialog dialogAddOrEditUser, dialogSelectAction, dialogUserDelete;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users);

        dbHelper = new DBHelper( this );
        gameHelper = new GameHelper(this.getBaseContext());
        //dbHelper.onCreate(dbHelper.getWritableDatabase());

        initUsersListView();
        initNewUserListView();
    }

    protected void onStart() {
        super.onStart();

        showNewUserList();
        showUsersList();
    }

    private void initUsersListView()
    {
        lvUsers = findViewById(R.id.lvUsers);
        lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int user_id = Integer.parseInt(usersData.get(position).get("user_id"));
                gameHelper.saveUserId2Preferences(user_id);
                Intent intent = new Intent(UsersActivity.this, UserExercisesActivity.class);
                startActivity(intent);
            }
        });

        lvUsers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                initDialogSelectAction(position);
                return true;
            }
        });
    }

    private void initNewUserListView()
    {
        lvNewUser = findViewById(R.id.lvNewUser);
        lvNewUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                initDialogAddOrEditUser(-1);
                dialogAddOrEditUser.show();
            }
        });
    }

    private void showUsersList()
    {
        usersData = dbHelper.getUsersData();
        lvUsers.invalidateViews();
        SimpleAdapter usersAdapter = new SimpleAdapter(
                this,
                usersData,
                android.R.layout.simple_list_item_2,
                new String[] {"name", "info", "user_id"},
                new int[] {android.R.id.text1, android.R.id.text2}
        );

        lvUsers.setAdapter(usersAdapter);
    }

    private void showNewUserList()
    {
        lvNewUser.invalidateViews();
        String[] items = {"Новый пользователь"};
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this, R.layout.new_user_list_item, items);

        lvNewUser.setAdapter(itemsAdapter);
    }

    private void initDialogAddOrEditUser(int current_user_id)
    {
        // get prompt_add_group.xmlgroup.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View prompts_view = li.inflate(R.layout.prompt_add_user, null);
        AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(this);
        alert_dialog_builder.setView(prompts_view);

        final EditText et_input_user_name = prompts_view.findViewById(R.id.editTextDialogUserInput);

        final int user_id = current_user_id;
        if (current_user_id != -1) {
            et_input_user_name.setText(dbHelper.getUserNameById(current_user_id));
        } else {
            et_input_user_name.setText("");
        }

        // set dialog message
        alert_dialog_builder
            .setCancelable(false)
            .setPositiveButton(R.string.btn_save_text,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                    String user_name = et_input_user_name.getText().toString();
                    if (user_name.trim().length() == 0) {
                        Toast.makeText(getBaseContext(), R.string.msg_user_need_name, Toast.LENGTH_SHORT).show();
                    } else if (dbHelper.isUserNameExist(user_name, user_id)) {
                        Toast.makeText(getBaseContext(), R.string.msg_user_name_already_exists, Toast.LENGTH_SHORT).show();
                    } else if (user_id == -1) {
                        long result = dbHelper.addUser(user_name);
                        if (result > 0) {
                            showUsersList();
                            Toast.makeText(getBaseContext(), R.string.msg_user_add_success, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(), R.string.msg_user_add_error, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        long result = dbHelper.updateUser(user_id, user_name);
                        if (result > 0) {
                            showUsersList();
                            Toast.makeText(getBaseContext(), R.string.msg_user_edit_success, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(), R.string.msg_user_edit_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                    }
                })
            .setNegativeButton(R.string.btn_cancel_text,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                    dialog.cancel();
                    }
                });

        // create alert dialog
        dialogAddOrEditUser = alert_dialog_builder.create();
    }


    private void initDialogSelectAction(int position)
    {
        final int final_position = position;
        LayoutInflater li = LayoutInflater.from(this);
        View prompts_view = li.inflate(R.layout.prompt_select_from_two_variants, null);

        AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(this);
        alert_dialog_builder.setView(prompts_view);
        alert_dialog_builder.setCancelable(true);
        dialogSelectAction = alert_dialog_builder.create();

        TextView tvTitle = prompts_view.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.prompt_select_action_title));

        Button btTitleLeft = prompts_view.findViewById(R.id.btTitleLeft);
        btTitleLeft.setText(getResources().getString(R.string.prompt_select_action_delete));
        btTitleLeft.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                initDialogAreYouSure(final_position);
                dialogSelectAction.cancel();
            }
        });

        Button btTitleRight = prompts_view.findViewById(R.id.btTitleRight);
        btTitleRight.setText(getResources().getString(R.string.prompt_select_action_edit));
        btTitleRight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int user_id = Integer.parseInt(usersData.get(final_position).get("user_id"));
                initDialogAddOrEditUser(user_id);
                dialogAddOrEditUser.show();
                dialogSelectAction.cancel();
            }
        });


        dialogSelectAction.show();
    }

    private void initDialogAreYouSure(int position)
    {
        final int final_position = position;
        LayoutInflater li = LayoutInflater.from(this);
        View prompts_view = li.inflate(R.layout.prompt_select_from_two_variants, null);

        AlertDialog.Builder alert_dialog_builder = new AlertDialog.Builder(this);
        alert_dialog_builder.setView(prompts_view);
        alert_dialog_builder.setCancelable(true);
        dialogUserDelete = alert_dialog_builder.create();

        final String final_username = usersData.get(final_position).get("name");
        TextView tvTitle = prompts_view.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.prompt_delete_user, final_username));


        Button btTitleLeft = prompts_view.findViewById(R.id.btTitleLeft);
        btTitleLeft.setText(getResources().getString(R.string.prompt_no));
        btTitleLeft.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialogUserDelete.cancel();
            }
        });

        Button btTitleRight = prompts_view.findViewById(R.id.btTitleRight);
        btTitleRight.setText(getResources().getString(R.string.prompt_yes));
        btTitleRight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dbHelper.exportDB("SportInTheForestDB_before_last_delete.db", false);
                dbHelper.exportDB("SportInTheForestDB_before_last_delete_" + gameHelper.getTodayString() + ".db", false);
                Toast.makeText(getBaseContext(), getResources().getString(R.string.prompt_delete_user_success, final_username), Toast.LENGTH_LONG).show();
                //TODO: delete user
                dialogUserDelete.cancel();
            }
        });

        dialogUserDelete.show();
    }

}