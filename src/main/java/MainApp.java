import java.io.IOException;
import java.security.GeneralSecurityException;

public class MainApp {
    public static void main(String[] args) throws GeneralSecurityException, IOException {
        System.out.println("\nInit App\n");

        final String spreadsheetId = "1pxY711YOoBl12np50v8e7FYneZ3zErv556HHxL390gw";
        final String rangeMaxClassesSemester = "engenharia_de_software!A2:A2";
        final String rangeGrades = "engenharia_de_software!A4:J";
        final String rangeInfoClasses = "engenharia_de_software!G4:H";
        final Long minPercentageClasses = 25L;


        ScholarGradesControl scholarGradesControl = new ScholarGradesControl(spreadsheetId, rangeGrades,
                rangeMaxClassesSemester, rangeInfoClasses, minPercentageClasses);

        scholarGradesControl.run();
    }
}
