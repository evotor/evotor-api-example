package ru.qualitylab.evotor.evotortest6;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ru.evotor.framework.core.IntegrationAppCompatActivity;
import ru.evotor.framework.users.Grant;
import ru.evotor.framework.users.User;
import ru.evotor.framework.users.UserApi;

/**
 * С помощью User API вы можете узнать данные всех пользователей или пользователя,
 * который авторизован на смарт-терминале в данный момент
 */
public class UserApiActivity extends IntegrationAppCompatActivity {

    TextView tvLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_api);

        tvLog = (TextView) findViewById(R.id.tvLog);

        findViewById(R.id.btnGetAllUsers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Получение списка всех пользователей терминала
                tvLog.setText("");
                List<User> users = UserApi.getAllUsers(getApplicationContext());
                StringBuilder sb = new StringBuilder();
                if (users != null) {
                    for (User user : users) {
                        sb.append("FirstName: ")
                                .append(user.getFirstName())
                                .append("\nSecondName: ")
                                .append(user.getSecondName())
                                .append("\nRoleTitle: ")
                                .append(user.getRoleTitle())
                                .append("\nPin: ")
                                .append(user.getPin())
                                .append("\nUuid: ")
                                .append(user.getUuid())
                                .append("\n");
                    }
                } else {
                    sb.append("null\n");
                }
                tvLog.setText(sb.toString());
            }
        });

        findViewById(R.id.btnGetAuthUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Получение данных авторизованного пользователя
                tvLog.setText("");
                User user = UserApi.getAuthenticatedUser(getApplicationContext());
                StringBuilder sb = new StringBuilder();
                sb.append("FirstName: ")
                        .append(user.getFirstName())
                        .append("\nSecondName: ")
                        .append(user.getSecondName())
                        .append("\nRoleTitle: ")
                        .append(user.getRoleTitle())
                        .append("\nPin: ")
                        .append(user.getPin())
                        .append("\nUuid: ")
                        .append(user.getUuid())
                        .append("\n");
                tvLog.setText(sb.toString());
            }
        });

        findViewById(R.id.btnGetAllGrants).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Получить список всех доступных прав
                tvLog.setText("");
                List<Grant> grants = UserApi.getAllGrants(getApplicationContext());
                StringBuilder sb = new StringBuilder();
                if (grants != null) {
                    for (Grant grant : grants) {
                        sb.append("RoleUuid: ")
                                .append(grant.getRoleUuid())
                                .append("\nTitle: ")
                                .append(grant.getTitle())
                                .append("\n");
                    }
                } else {
                    sb.append("null\n");
                }
                tvLog.setText(sb.toString());
            }
        });

        findViewById(R.id.btnGetGrantsAuthUsers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Получить список прав для авторизованного пользователя
                tvLog.setText("");
                List<Grant> grants = UserApi.getGrantsOfAuthenticatedUser(getApplicationContext());
                StringBuilder sb = new StringBuilder();
                if (grants != null) {
                    for (Grant grant : grants) {
                        sb.append("RoleUuid: ")
                                .append(grant.getRoleUuid())
                                .append("\nTitle: ")
                                .append(grant.getTitle())
                                .append("\n");
                    }
                } else {
                    sb.append("null\n");
                }
                tvLog.setText(sb.toString());
            }
        });
    }
}
