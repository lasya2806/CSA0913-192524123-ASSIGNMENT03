
class ExamException extends Exception {

    ExamException(String message) {
        super(message);
    }

    ExamException(String message, Throwable cause) {
        super(message, cause);
    }
}

class ExamClosedException extends ExamException {
    ExamClosedException(String message) {
        super(message);
    }
}

class StudentNotRegisteredException extends ExamException {
    StudentNotRegisteredException(String message) {
        super(message);
    }
}

class SubmissionStorageException extends ExamException {
    SubmissionStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}

class Storage {

    void save(String fileName, long sizeKb)
            throws SubmissionStorageException {

        try {
            if (sizeKb > 500) {
                throw new java.io.IOException("Disk quota exceeded");
            }

            System.out.println("Saved " + fileName);

        } catch (java.io.IOException e) {

            throw new SubmissionStorageException(
                "Could not store " + fileName, e);
        }
    }
}

class ExamService {

    void submit(boolean registered, boolean examOpen,
                String fileName, long sizeKb)
                throws ExamException {

        if (!registered) {
            throw new StudentNotRegisteredException(
                "Student is not registered");
        }

        if (!examOpen) {
            throw new ExamClosedException(
                "Exam submission window is closed");
        }

        Storage storage = new Storage();
        storage.save(fileName, sizeKb);
    }
}

public class OnlineExamPortal {

    public static void main(String[] args) {

        ExamService service = new ExamService();

        try {

            service.submit(true, true, "answer01.pdf", 120);

            service.submit(true, true, "answer02.pdf", 800);

        } catch (ExamException e) {

            System.out.println("Error: " + e.getMessage());

            if (e.getCause() != null) {
                System.out.println(
                    "Cause: " + e.getCause().getMessage());
            }

        } finally {
            System.out.println("Exam Submission Process Completed.");
        }
    }
}

