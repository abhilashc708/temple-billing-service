package com.example.temple_billing.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DatabaseBackupService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseBackupService.class);
    private static String backUpPath = null;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${backup.mysql.mac.path}")
    private String macPath;

    @Value("${backup.mysql.windows.path}")
    private String windowsPath;

    @Value("${backup.output.dir.windows}")
    private String windowsBackupDir;

    @Value("${backup.output.dir.mac}")
    private String macBackupDir;

    @Value("${backup.file.prefix}")
    private String filePrefix;

    @Value("${backup.retain.count}")
    private int retainCount;

    // RUN DAILY AT 6 PM
    //@Scheduled(cron = "0 0 18 * * *")
    public void backupDatabase() {
        try {
            logger.info("🔄 Starting DB Backup...");

            // ✅ Extract DB name from URL
            String dbName = dbUrl.substring(dbUrl.lastIndexOf("/") + 1);

            // ✅ Detect OS
            String os = System.getProperty("os.name").toLowerCase();
            String dumpPath = os.contains("win") ? windowsPath : macPath;
            backUpPath = os.contains("win") ? windowsBackupDir : macBackupDir;

            logger.info("Using mysqldump: {}", dumpPath);
            logger.info("Database: {}", dbName);

            // ✅ Create backup folder if not exists
            File dir = new File(backUpPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // ✅ File name
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String backupFile = backUpPath + "/" + filePrefix + timestamp + ".sql";

            // ✅ Build command
            List<String> command = new ArrayList<>();
            command.add(dumpPath);
            command.add("-u");
            command.add(dbUser);
            command.add(dbName);

            ProcessBuilder pb = new ProcessBuilder(command);

            // 🔥 Secure password (BEST WAY)
            pb.environment().put("MYSQL_PWD", dbPassword);

            pb.redirectOutput(new File(backupFile));
            pb.redirectErrorStream(true);

            Process process = pb.start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                logger.info("✅ Backup SUCCESS: {}", backupFile);

                // 🔥 Keep only last N backups
                cleanOldBackups();

            } else {
                logger.error("❌ Backup FAILED (Exit Code: {})", exitCode);
            }

        } catch (Exception e) {
            logger.error("Error during database backup", e);
        }
    }

    // 🔥 KEEP ONLY LAST N FILES
    private void cleanOldBackups() {

        File folder = new File(backUpPath);

        File[] files = folder.listFiles((dir, name) -> name.startsWith(filePrefix));

        if (files == null || files.length <= retainCount) return;

        // ✅ Sort oldest first
        Arrays.sort(files, Comparator.comparingLong(File::lastModified));

        int filesToDelete = files.length - retainCount;

        for (int i = 0; i < filesToDelete; i++) {
            logger.info("🗑 Deleting: {}", files[i].getName());
            files[i].delete();
        }
    }
}