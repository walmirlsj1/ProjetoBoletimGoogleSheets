
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.security.GeneralSecurityException;

import google_sheets_api.GetValues;
import google_sheets_api.UpdateValues;

public class ScholarGradesControl {
    private final String SPREADSHEET_ID;
    private final String RANGE_MAX_DAY_CLASSES;
    private final String RANGE_INFO_CLASS;
    private final String RANGE_GRADES;
    private final Long MIN_PERCENTAGE_CLASSES;

    private Long minClassesSemester;
    private Long maxClassesSemester;


    public ScholarGradesControl(String spreadsheetId, String rangeGrades, String rangeMaxClassesSemester, String rangeUpdateInfoClass, Long minPercentageClasses) {
        this.SPREADSHEET_ID = spreadsheetId;
        this.RANGE_GRADES = rangeGrades;
        this.RANGE_MAX_DAY_CLASSES = rangeMaxClassesSemester;
        this.RANGE_INFO_CLASS = rangeUpdateInfoClass;
        this.MIN_PERCENTAGE_CLASSES = minPercentageClasses;
    }

    private void calcMinimumDayClasses() {
        minClassesSemester = Math.round(maxClassesSemester * MIN_PERCENTAGE_CLASSES / 100.0);
        System.out.println("Minimum day of classes: " + minClassesSemester);
    }

    private CourseStatusEnum getCourseStatus(Long average, Integer missedClasses) {
        if (checkMissedClasses(missedClasses))
            return CourseStatusEnum.REPROVADO_POR_FALTA;
        else if (average < 50L)
            return CourseStatusEnum.REPROVADO_POR_NOTA;
        else if (average < 70L)
            return CourseStatusEnum.EXAME_FINAL;

        return CourseStatusEnum.APROVADO;
    }

    private Long calcScoreNeedAcceptance(Long average) {
        return 100L - average;
    }

    private Long calcAverage(Double examScore1, Double examScore2, Double examScore3) {
        System.out.println("Calculate exam scores average");
        return Math.round((examScore1 + examScore2 + examScore3) / 3);
    }

    private Boolean checkMissedClasses(Integer missedClasses) {
        return missedClasses > minClassesSemester;
    }

    private void getMaxDayClassesOnSpreadsheet() throws GeneralSecurityException, IOException {
        var sheetValueRange = GetValues.getValues(SPREADSHEET_ID, RANGE_MAX_DAY_CLASSES)
                .getValues();

        System.out.println("Getting the semester total of classes");

        String maxClassesSemesterString = sheetValueRange.get(0).get(0).toString();
        maxClassesSemesterString = maxClassesSemesterString.replaceAll("Total de aulas no semestre: ", "");
        maxClassesSemester = Long.valueOf(maxClassesSemesterString);

        this.calcMinimumDayClasses();
    }

    private void manageExamScores() throws GeneralSecurityException, IOException {
        System.out.println("Getting semester grades and student information");

        var values = GetValues.getValues(SPREADSHEET_ID, RANGE_GRADES)
                .getValues();

        List<List<Object>> dataInsert = new ArrayList<List<Object>>();
        List<Object> register;
        Long averageNaf;
        Long average;
        CourseStatusEnum courseStatus;

        for (List<?> row : values) {
            averageNaf = 0L;

            var studentEnrolled = Integer.parseInt(row.get(0).toString());
            var studentName = (String) row.get(1);
            var studentMissedClasses = Integer.parseInt(row.get(2).toString());
            var studentExamScore1 = Double.parseDouble(row.get(3).toString());
            var studentExamScore2 = Double.parseDouble(row.get(4).toString());
            var studentExamScore3 = Double.parseDouble(row.get(5).toString());

            System.out.printf("Read registry %d, %s, %d, %.2f, %.2f, %.2f\n", studentEnrolled, studentName, studentMissedClasses
                    , studentExamScore1, studentExamScore2, studentExamScore3);
            average = calcAverage(studentExamScore1, studentExamScore2, studentExamScore3);
            System.out.println("Average: " + average);

            courseStatus = getCourseStatus(average, studentMissedClasses);
            System.out.println("Course status: " + courseStatus);


            if (courseStatus.equals(CourseStatusEnum.EXAME_FINAL)) {
                averageNaf = calcScoreNeedAcceptance(average);
                System.out.println("Exam score to be achieved: " + averageNaf);

            }
            register = new ArrayList<>();
            register.add(courseStatusEnumToString(courseStatus));
            register.add(averageNaf);


            dataInsert.add(register);
            System.out.println();
        }

        persistWorksheet(dataInsert);
    }

    private String courseStatusEnumToString(CourseStatusEnum courseStatusEnum) {

        switch (courseStatusEnum) {
            case REPROVADO_POR_NOTA:
                return "Reprovado por Nota";
            case REPROVADO_POR_FALTA:
                return "Reprovado por Falta";
            case APROVADO:
                return "Aprovado";
            case EXAME_FINAL:
                return "Exame Final";
        }
        throw new RuntimeException("Fail access CourseStatusEnum");
    }

    private void persistWorksheet(List<List<Object>> values) throws GeneralSecurityException, IOException {
        System.out.println("Saving worksheet changes");
        UpdateValues.updateValues(SPREADSHEET_ID, RANGE_INFO_CLASS, "RAW", values);
    }

    public void run() throws GeneralSecurityException, IOException {
        this.getMaxDayClassesOnSpreadsheet();
        this.manageExamScores();
    }

}
