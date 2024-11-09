package jdbc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import java.sql.*;
import java.util.*;

public class company extends JFrame implements ActionListener {
	
	private Connection conn;
	
	private JLabel dbnameLabel = new JLabel("DB명: ");
	private JLabel userLabel = new JLabel("user: ");
	private JLabel pwdLabel = new JLabel("password: ");
	private JLabel searchLabel = new JLabel("검색 항목: ");
	private JLabel empCountLabel = new JLabel("인원 수: ");
	private JLabel empCount = new JLabel("0");
	
	private JTextField dbnameText = new JTextField(10);
	private JTextField userText = new JTextField(10);
	private JTextField pwdText = new JTextField(10);
	
	private JCheckBox nameCB = new JCheckBox("Name", true);
	private JCheckBox ssnCB = new JCheckBox("Ssn", true);
	private JCheckBox bDateCB = new JCheckBox("Bdate", true);
	private JCheckBox addressCB = new JCheckBox("Address", true);
	private JCheckBox sexCB = new JCheckBox("Sex", true);
	private JCheckBox salaryCB = new JCheckBox("Salary", true);
	private JCheckBox supervisorCB = new JCheckBox("Supervisor", true);
	private JCheckBox departmentCB = new JCheckBox("Department", true);
	
	private JButton connBtn = new JButton("연결");
	private JButton searchBtn = new JButton("검색");
	
	private JTable resultTable;
	private DefaultTableModel resultModel;
	private JScrollPane scrollPane;

	private ArrayList<String> columnNames = new ArrayList<String>();
	
	//추가한 부분
	private JComboBox<String> conditionComboBox = new JComboBox<>(new String[]{"전체", "부서", "성별", "연봉"});
	private JComboBox<String> departmentComboBox = new JComboBox<>(new String[]{"Research", "Headquarters", "Administration"});
	private JComboBox<String> sexComboBox = new JComboBox<>(new String[]{"M", "F"});
	private JTextField salaryTextField = new JTextField(10);
	private JComboBox<String> groupConditionComboBox = new JComboBox<>(new String[]{"그룹 없음", "성별", "부서", "상급자"});



	
	public company() {

		// DB 연결 관련 코드
		JPanel dbConnPanel = new JPanel();
		dbConnPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		dbConnPanel.add(dbnameLabel);
		dbConnPanel.add(dbnameText);
		dbConnPanel.add(userLabel);
		dbConnPanel.add(userText);
		dbConnPanel.add(pwdLabel);
		dbConnPanel.add(pwdText);
		dbConnPanel.add(connBtn);
		
		// 검색 관련 코드
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		searchPanel.add(searchLabel);
		searchPanel.add(nameCB);
		searchPanel.add(ssnCB);
		searchPanel.add(bDateCB);
		searchPanel.add(addressCB);
		searchPanel.add(sexCB);
		searchPanel.add(salaryCB);
		searchPanel.add(supervisorCB);
		searchPanel.add(departmentCB);
		searchPanel.add(searchBtn);
		
		// 결과창 관련 코드
		JPanel resultPanel = new JPanel();
		resultPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        resultModel = new DefaultTableModel();
        resultTable = new JTable(resultModel);
        scrollPane = new JScrollPane(resultTable);
        scrollPane.setPreferredSize(new Dimension(1000, 400));
		
		resultPanel.add(scrollPane);
		//추가된 내용
		JPanel conditionPanel = new JPanel();
		conditionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		conditionPanel.add(new JLabel("검색 범위:"));
		conditionPanel.add(conditionComboBox);
		conditionPanel.add(departmentComboBox);
		conditionPanel.add(sexComboBox);
		conditionPanel.add(salaryTextField);

		// 기본적으로 부서, 성별, 연봉 입력 필드는 비활성화 또는 숨김 상태
		departmentComboBox.setVisible(false);
		sexComboBox.setVisible(false);
		salaryTextField.setVisible(false);
		
		
		// 그룹별 평균 월급 조건 UI 패널 설정
		JPanel groupConditionPanel = new JPanel();
		groupConditionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		groupConditionPanel.add(new JLabel("그룹별 평균 월급:"));
		groupConditionPanel.add(groupConditionComboBox);

		JPanel combinedConditionPanel = new JPanel();
		combinedConditionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		combinedConditionPanel.add(conditionPanel);
		combinedConditionPanel.add(groupConditionPanel);

		
		

		
		// 하나의 큰 Top panel에 DB 연결, 검색, 결과창 panel을 집어넣음
		JPanel Top = new JPanel();
		Top.setLayout(new BoxLayout(Top, BoxLayout.Y_AXIS));
		Top.add(dbConnPanel);
		Top.add(searchPanel);
		Top.add(combinedConditionPanel);
		Top.add(resultPanel);

		add(Top, BorderLayout.NORTH);
		
		
		
		
		// 버튼과 이벤트 처리 함수 연결
		connBtn.addActionListener(this);
		searchBtn.addActionListener(this);
		//추가내용
		conditionComboBox.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        String selectedCondition = (String) conditionComboBox.getSelectedItem();

		        // 모든 입력 필드를 숨깁니다
		        departmentComboBox.setVisible(false);
		        sexComboBox.setVisible(false);
		        salaryTextField.setVisible(false);

		        // 선택된 조건에 따라 필드를 표시합니다
		        if ("부서".equals(selectedCondition)) {
		            departmentComboBox.setVisible(true);
		        } else if ("성별".equals(selectedCondition)) {
		            sexComboBox.setVisible(true);
		        } else if ("연봉".equals(selectedCondition)) {
		            salaryTextField.setVisible(true);
		        }

		        // 패널을 다시 그려서 변경 사항이 반영되도록 합니다
		        conditionPanel.revalidate();
		        conditionPanel.repaint();
		    }
		});
		
		setSize(1200, 800); // 창 크기 세팅
		setLocationRelativeTo(null); // 창이 가운데 위치하도록
		setTitle("103조 JDBC 프로젝트"); // 타이틀 세팅
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창을 닫았을 때 프로그램이 종료되도록 설정
		setVisible(true); // 창이 보이도록
	}

	// 이벤트 발생 처리 함수
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == connBtn) { // DB 연결 버튼이 클릭되었을 때

			try {
				String dbname = dbnameText.getText();
				String user = userText.getText();
				String pwd = pwdText.getText();
				
				String url = "jdbc:mysql://localhost:3306/" + dbname + "?serverTimeZone=UTC";
				conn = DriverManager.getConnection(url, user, pwd);
				System.out.println("DB에 연결되었습니다.");
				connBtn.setForeground(Color.blue);
				connBtn.setText("연결 완료!!");
						
			} catch (SQLException e1) {
				System.err.println("DB 연결에 실패했습니다.");
				connBtn.setForeground(Color.red);
				connBtn.setText("연결 실패");
				e1.printStackTrace();
			}
		}

		if (e.getSource() == searchBtn) { // 검색 버튼이 클릭되었을 때
		
			
			String query; // select 하는 쿼리 가져오기
			
			if (!"그룹 없음".equals(groupConditionComboBox.getSelectedItem())) {
		        query = getGroupQuery(); // 그룹별 평균 월급 및 검색 조건이 적용된 쿼리
		        
		        // 그룹별 평균 월급에 따라 열 이름을 설정합니다.
		        columnNames.clear();
		        String selectedGroup = (String) groupConditionComboBox.getSelectedItem();
		        if ("부서".equals(selectedGroup)) {
		            columnNames.add("Dname"); // 부서명을 표시하는 열 이름
		        } else if ("성별".equals(selectedGroup)) {
		            columnNames.add("SEX"); // 성별을 표시하는 열 이름
		        } else if ("상급자".equals(selectedGroup)) {
		            columnNames.add("Supervisor"); // 상급자 이름을 표시하는 열 이름
		        }
		        columnNames.add("AVG_Salary"); // 평균 월급 표시
		    } else {
			    query = getConditionsQuery(); // where 조건 추가 검색
			}
			
			if (query.length() > 0) {
				try {
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(query);
					int colCount = rs.getMetaData().getColumnCount(); // 열의 갯수 체크

					resultModel = new DefaultTableModel(columnNames.toArray(), 0);
					while (rs.next()) {
						ArrayList<Object> row = new ArrayList<>();
						for (int i = 1; i <= colCount; i++) {
							row.add(rs.getObject(i));
						}
						
						resultModel.addRow(row.toArray());
					}
					
					resultTable = new JTable(resultModel);
					scrollPane.setViewportView(resultTable);
					revalidate();
					repaint();
				}
				catch (SQLException e1) {
					e1.printStackTrace();
					System.err.println("DB 검색 중 실패했습니다.");
				}
			}
			else {
				System.err.println("선택된 attribute가 없습니다.");
			}
		}
	}

	// 검색 쿼리 가져오는 함수
	private String getQuery() {
		int selectedCount = 0;
		String query = "select ";
		columnNames.clear();
		
		if (nameCB.isSelected()) {
			query += "CONCAT(Fname, ' ', Minit, ' ', Lname)";
			selectedCount += 1;
			columnNames.add("NAME");
		}

		if (ssnCB.isSelected()) {
			if (selectedCount != 0) {
				query += ",";
			}
			query += "ssn";
			selectedCount += 1;
			columnNames.add("SSN");
		}

		if (bDateCB.isSelected()) {
			if (selectedCount != 0) {
				query += ",";
			}
			query += "Bdate";
			selectedCount += 1;
			columnNames.add("BDATE");
		}

		if (addressCB.isSelected()) {
			if (selectedCount != 0) {
				query += ",";
			}
			query += "address";
			selectedCount += 1;
			columnNames.add("ADDRESS");
		}

		if (sexCB.isSelected()) {
			if (selectedCount != 0) {
				query += ",";
			}
			query += "sex";
			selectedCount += 1;
			columnNames.add("SEX");
		}

		if (salaryCB.isSelected()) {
			if (selectedCount != 0) {
				query += ",";
			}
			query += "salary";
			selectedCount += 1;
			columnNames.add("SALARY");
		}

		if (supervisorCB.isSelected()) {
			if (selectedCount != 0) {
				query += ",";
			}
			query += "(select CONCAT(Fname, ' ', Minit, ' ', Lname) from company.Employee where ssn = E.Super_ssn)";
			selectedCount += 1;
			columnNames.add("SUPERVISOR");
		}

		if (departmentCB.isSelected()) {
			if (selectedCount != 0) {
				query += ",";
			}
			query += "(select Dname from company.department where Dnumber = E.Dno)";
			selectedCount += 1;
			columnNames.add("DEPARTMENT");
		}
		
		query += " from company.employee E";
		
		if (selectedCount == 0) {
			query = "";
		}
		
		return query;
	}
	
	private String getConditionsQuery() {
	    String query = getQuery(); //일반 쿼리문 가져오기
	    StringBuilder condition = new StringBuilder();

	    String selectedCondition = (String) conditionComboBox.getSelectedItem();
	    if ("부서".equals(selectedCondition)) {
	        condition.append(" E.Dno = (SELECT Dnumber FROM department WHERE Dname = '")
	                 .append(departmentComboBox.getSelectedItem())
	                 .append("')");
	    } else if ("성별".equals(selectedCondition)) {
	        condition.append("sex = '").append(sexComboBox.getSelectedItem()).append("'");
	    } else if ("연봉".equals(selectedCondition) && !salaryTextField.getText().isEmpty()) {
	        condition.append("salary >= ").append(salaryTextField.getText());
	    }

	    if (condition.length() > 0) {
	        query += " WHERE " + condition.toString();
	    }

	    return query;
	}
	
	
	private String getGroupQuery() {
	    String selectedGroup = (String) groupConditionComboBox.getSelectedItem();
	    StringBuilder query = new StringBuilder();
	    StringBuilder whereClause = new StringBuilder();

	    // 기본 쿼리의 FROM 및 JOIN 절 구성
	    if ("부서".equals(selectedGroup)) {
	        query.append("SELECT D.Dname AS `Group`, AVG(E.salary) AS AVG_Salary ")
	             .append("FROM company.employee E ")
	             .append("JOIN company.department D ON E.Dno = D.Dnumber ");
	    } else if ("상급자".equals(selectedGroup)) {
	        query.append("SELECT CONCAT(S.Fname, ' ', S.Minit, ' ', S.Lname) AS `Group`, AVG(E.salary) AS AVG_Salary ")
	             .append("FROM company.employee E ")
	             .append("JOIN company.employee S ON E.Super_ssn = S.ssn ");
	    } else if ("성별".equals(selectedGroup)) {
	        query.append("SELECT E.sex AS `Group`, AVG(E.salary) AS AVG_Salary ")
	             .append("FROM company.employee E ");
	    }

	    // 검색 범위 조건 추가
	    String selectedCondition = (String) conditionComboBox.getSelectedItem();
	    if ("부서".equals(selectedCondition)) {
	        whereClause.append("E.Dno = (SELECT Dnumber FROM company.department WHERE Dname = '")
	                   .append(departmentComboBox.getSelectedItem())
	                   .append("')");
	    } else if ("성별".equals(selectedCondition)) {
	        whereClause.append("E.sex = '").append(sexComboBox.getSelectedItem()).append("'");
	    } else if ("연봉".equals(selectedCondition) && !salaryTextField.getText().isEmpty()) {
	        whereClause.append("E.salary >= ").append(salaryTextField.getText());
	    }

	    // WHERE 절이 있다면 추가
	    if (whereClause.length() > 0) {
	        query.append("WHERE ").append(whereClause.toString()).append(" ");
	    }

	    // GROUP BY 절 추가
	    if ("부서".equals(selectedGroup)) {
	        query.append("GROUP BY D.Dname");
	    } else if ("상급자".equals(selectedGroup)) {
	        query.append("GROUP BY E.Super_ssn");
	    } else if ("성별".equals(selectedGroup)) {
	        query.append("GROUP BY E.sex");
	    }

	    return query.toString();
	}





	
	public static void main(String[] args) {
		new company();

	}

}
