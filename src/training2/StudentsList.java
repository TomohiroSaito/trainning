package training2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentsList {

	public static void main(String[] args) {
		outMessage();
		int classId = inputClass();
		selectClasses(classId);
	}

	static void outMessage() {
		System.out.println("確認するクラスを入力してください。(A,B,Cのいずれか)");
	}

	static void outMessage(int number, String className, String studentName) {
		String message = String.format("生徒番号:%d\tクラス名:%s\t生徒名:%s", number, className, studentName);
		System.out.println(message);
	}

	static int inputClass() {
		String studentClass = input();
		return classFormat(studentClass);
	}

	static int classFormat(String target) {
		classError(target);
		return getClassId(target);
	}

	static String input() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String inputString = null;
		try {
			inputString = reader.readLine();
		} catch(IOException e) {
			System.out.println("入力時にエラーが起こりました。");
			System.exit(-1);
		}
		return inputString;
	}

	static void classError(String target) {
		if(!target.matches("A|B|C")) {
			System.out.println("クラスはA,B,Cの中から選んでください。");
			System.exit(-1);
		}

	}

	static int getClassId(String target) {
		int classId = 0;
		switch(target) {
		case "A" :
			classId = 1;
			break;
		case "B" :
			classId = 2;
			break;
		case "C" :
			classId = 3;
			break;
		}
		return classId;
	}

	static void selectClasses(int classId) {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;

		//接続文字列
		String url = "jdbc:postgresql://localhost:5433/postgres";
		String user = "postgres";
		String password = "postgres";

		try {
			Class.forName("org.postgresql.Driver");//ClassNotFoundException投げる

			//PostgreSQLへ接続
			conn = DriverManager.getConnection(url, user, password);

			//自動コミットOFF
			conn.setAutoCommit(false);

			//SELECT文の実行
			String sql = "SELECT Student.number,Class.class_name,Student.name FROM Student INNER JOIN Class ON Student.class = Class.class_id WHERE Student.class=?";
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, classId);
			rs = preparedStatement.executeQuery();
			int number = 0;
			String className = null;
			String studentName = null;
			while(rs.next()) {
				number = rs.getInt("number");
				className = rs.getString("class_name");
				studentName = rs.getString("name");
				outMessage(number, className, studentName);
			}
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(preparedStatement != null) preparedStatement.close();
				if(rs != null) rs.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
