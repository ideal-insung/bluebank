package bank.core.com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCConnectionTest {

    public static void main(String[] args) {
        // JDBC URL, 사용자명, 비밀번호
        String jdbcUrl = "jdbc:mysql://localhost:3306/ideal?useSSL=false&serverTimezone=UTC";
        String username = "root";
        String password = "xa4613";

        // MySQL 연결
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            System.out.println("MySQL 연결 성공!");

            // 쿼리 실행 예시
            String query = "SELECT NOW()"; // 현재 시간을 가져오는 SQL 쿼리
            try (Statement statement = connection.createStatement()) {
                statement.execute(query);
                System.out.println("쿼리 실행 성공");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("MySQL 연결 실패!");
        }
    }
}