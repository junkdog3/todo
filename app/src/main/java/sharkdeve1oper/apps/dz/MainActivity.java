package sharkdeve1oper.apps.dz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> tasks = new ArrayList<>();
    Set<String> completedTasks = new HashSet<>();
    ArrayAdapter<String> arrayAdapter;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);
        Button addButton = findViewById(R.id.addButton);
        final EditText editText = findViewById(R.id.editText);

        sharedPreferences = getSharedPreferences("com.example.todo", MODE_PRIVATE);
        Set<String> taskSet = sharedPreferences.getStringSet("tasks", null);
        Set<String> completedSet = sharedPreferences.getStringSet("completedTasks", null);

        if (taskSet != null) {
            tasks.addAll(taskSet);
        }

        if (completedSet != null) {
            completedTasks.addAll(completedSet);
        }

        sortTasks();

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tasks) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                String task = textView.getText().toString();
                if (completedTasks.contains(task)) {
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    textView.setTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.darker_gray));
                } else {
                    textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    textView.setTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.black));
                }
                return textView;
            }
        };
        listView.setAdapter(arrayAdapter);

        addButton.setOnClickListener(v -> {
            String task = editText.getText().toString();
            if (!task.isEmpty()) {
                if (tasks.contains(task)) {
                    Toast.makeText(MainActivity.this, "Задача уже существует", Toast.LENGTH_SHORT).show();
                } else {
                    tasks.add(task);
                    sortTasks();
                    arrayAdapter.notifyDataSetChanged();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Set<String> set = new HashSet<>(MainActivity.this.tasks);
                    editor.putStringSet("tasks", set);
                    editor.apply();

                    editText.setText("");
                }
            } else {
                Toast.makeText(MainActivity.this, "Введите задачу", Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            TextView textView = (TextView) view;
            String task = textView.getText().toString();
            if ((textView.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) > 0) {
                textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                textView.setTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.black));
                completedTasks.remove(task);
            } else {
                textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                textView.setTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.darker_gray));
                completedTasks.add(task);
            }

            sortTasks();
            arrayAdapter.notifyDataSetChanged();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            Set<String> set = new HashSet<>(MainActivity.this.completedTasks);
            editor.putStringSet("completedTasks", set);
            editor.apply();
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Удалить задачу");
            builder.setMessage("Вы уверены, что хотите удалить эту задачу?");
            builder.setPositiveButton("Да", (dialog, which) -> {
                String task = tasks.get(position);
                tasks.remove(position);
                completedTasks.remove(task); // remove task from completedTasks
                sortTasks();
                arrayAdapter.notifyDataSetChanged();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                Set<String> set = new HashSet<>(MainActivity.this.tasks);
                editor.putStringSet("tasks", set);
                Set<String> completedSet1 = new HashSet<>(MainActivity.this.completedTasks);
                editor.putStringSet("completedTasks", completedSet1); // save updated completedTasks
                editor.apply();
            });
            builder.setNegativeButton("Нет", null);
            builder.show();

            return true;
        });
    }

    private void sortTasks() {
        Collections.sort(tasks, (a, b) -> {
            if (completedTasks.contains(a) && !completedTasks.contains(b)) {
                return 1;
            } else if (!completedTasks.contains(a) && completedTasks.contains(b)) {
                return -1;
            } else {
                return a.compareTo(b);
            }
        });
    }
}
