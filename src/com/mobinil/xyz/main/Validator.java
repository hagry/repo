package com.mobinil.xyz.main;

import com.mobinil.xyz.logger.SANDLogger;
import com.mobinil.xyz.util.ConfigParameters;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.regex.PatternSyntaxException;

public class Validator extends Thread {

    private Properties mailProperties = new Properties();
    private Properties logProperties = new Properties();
    private String temp, line;

    @Override
    public void run() {
        validateProperties();
    }

    private void validateProperties() {
        try {

            // Mail Properties File
            mailProperties.load(ClassLoader.getSystemResourceAsStream("ExchangeConnector.properties"));
            // Mail Username Check
            if (mailProperties.get(ConfigParameters.MAIL_USERNAME) != null) {
                ConfigParameters.MAIL_USERNAME_VALUE = mailProperties.get(
                        ConfigParameters.MAIL_USERNAME).toString();
            } else {
                SANDLogger.getLogger().error(
                        ConfigParameters.MAIL_USERNAME + " can't be found");
            }
            // Mail Password Check
            if (mailProperties.get(ConfigParameters.MAIL_PASSWORD) != null) {
                ConfigParameters.MAIL_PASSWORD_VALUE = mailProperties.get(
                        ConfigParameters.MAIL_PASSWORD).toString();
            } else {
                SANDLogger.getLogger().error(
                        ConfigParameters.MAIL_PASSWORD + " can't be found");
            }
            // Sleep Time Check
            if (mailProperties.get(ConfigParameters.SLEEP_TIME) != null) {
                try {
                    ConfigParameters.SLEEP_TIME_VALUE = Integer.parseInt(mailProperties.get(
                            ConfigParameters.SLEEP_TIME).toString()) * 60 * 1000;
                } catch (NumberFormatException e) {
                    SANDLogger.getLogger().error(
                            ConfigParameters.SLEEP_TIME + " value is invalid");
                }
            } else {
                SANDLogger.getLogger().error(
                        ConfigParameters.SLEEP_TIME + " can't be found");
            }
            // Fetch Size Check
            if (mailProperties.get(ConfigParameters.FETCH_SIZE) != null) {
                try {
                    ConfigParameters.FETCH_SIZE_VALUE = Integer.parseInt(mailProperties.get(
                            ConfigParameters.FETCH_SIZE).toString());
                } catch (NumberFormatException e) {
                    SANDLogger.getLogger().error(
                            ConfigParameters.FETCH_SIZE + " value is invalid");
                    ConfigParameters.FETCH_SIZE_VALUE = 100;
                }
            } else {
                SANDLogger.getLogger().error(
                        ConfigParameters.FETCH_SIZE + " can't be found");
                ConfigParameters.FETCH_SIZE_VALUE = 100;
            }
            // Max Records Number Check
            if (mailProperties.get(ConfigParameters.MAX_RECORD_NUMBER) != null) {
                try {
                    ConfigParameters.MAX_RECORD_NUMBER_VALUE = Integer.parseInt(mailProperties.get(
                            ConfigParameters.MAX_RECORD_NUMBER).toString() + 2);
                } catch (NumberFormatException e) {
                    SANDLogger.getLogger().error(
                            ConfigParameters.MAX_RECORD_NUMBER + " value is invalid");
                }
            } else {
                SANDLogger.getLogger().error(
                        ConfigParameters.MAX_RECORD_NUMBER + " can't be found");
            }
            // Max Number of Files/POS Check
            if (mailProperties.get(ConfigParameters.MAX_NUMBER_PER_POS) != null) {
                try {
                    ConfigParameters.MAX_NUMBER_PER_POS_VALUE = Integer.parseInt(mailProperties.get(
                            ConfigParameters.MAX_NUMBER_PER_POS).toString());
                } catch (NumberFormatException e) {
                    SANDLogger.getLogger().error(
                            ConfigParameters.MAX_NUMBER_PER_POS + " value is invalid");
                }
            } else {
                SANDLogger.getLogger().error(ConfigParameters.MAX_NUMBER_PER_POS + " can't be found");
            }
            // Testing Mode Check
            if (mailProperties.get(ConfigParameters.TESTING) != null) {
                try {
                    ConfigParameters.TESTING_VALUE = Boolean.parseBoolean(mailProperties.get(
                            ConfigParameters.TESTING).toString());
                } catch (NumberFormatException e) {
                    SANDLogger.getLogger().error(
                            ConfigParameters.TESTING + " value is invalid");
                }
            } else {
                SANDLogger.getLogger().error(
                        ConfigParameters.TESTING + " can't be found");
            }
            // Testing regx
            if (mailProperties.get(ConfigParameters.TESTING_REGX) != null) {
                ConfigParameters.TESTING_VALUE_REGX = mailProperties.get(
                        ConfigParameters.TESTING_REGX).toString();
            } else {
                SANDLogger.getLogger().error(
                        ConfigParameters.TESTING_REGX + " can't be found");
            }
            // Delete excel History Mode Check
            if (mailProperties.get(ConfigParameters.DELETE_EXCEL_HISTORY) != null) {
                try {
                    ConfigParameters.DELETE_EXCEL_HISTORY_VALUE = Boolean.parseBoolean(mailProperties.get(
                            ConfigParameters.DELETE_EXCEL_HISTORY).toString());
                } catch (NumberFormatException e) {
                    SANDLogger.getLogger().error(
                            ConfigParameters.DELETE_EXCEL_HISTORY + " value is invalid");
                }
            } else {
                SANDLogger.getLogger().error(
                        ConfigParameters.DELETE_EXCEL_HISTORY + " can't be found");
            }
            // Delete mail History Mode Check
            if (mailProperties.get(ConfigParameters.DELETE_HISTORY) != null) {
                try {
                    ConfigParameters.DELETE_HISTORY_VALUE = Boolean.parseBoolean(mailProperties.get(
                            ConfigParameters.DELETE_HISTORY).toString());
                } catch (NumberFormatException e) {
                    SANDLogger.getLogger().error(
                            ConfigParameters.DELETE_HISTORY + " value is invalid");
                }
            } else {
                SANDLogger.getLogger().error(
                        ConfigParameters.DELETE_HISTORY + " can't be found");
            }
            // Keep History Days Check
            if (mailProperties.get(ConfigParameters.HISTORY_KEEP_DAYS) != null) {
                try {
                    ConfigParameters.HISTORY_KEEP_DAYS_VALUE = Integer.parseInt(mailProperties.get(
                            ConfigParameters.HISTORY_KEEP_DAYS).toString());
                } catch (NumberFormatException e) {
                    SANDLogger.getLogger().error(
                            ConfigParameters.HISTORY_KEEP_DAYS + " value is invalid");
                }
            } else {
                SANDLogger.getLogger().error(
                        ConfigParameters.HISTORY_KEEP_DAYS + " can't be found");
            }
            // Excel MIME types Check
            if (mailProperties.get(ConfigParameters.EXCEL_MIME_TYPES) != null) {
                try {
                    ConfigParameters.EXCEL_MIME_TYPES_VALUE = mailProperties.get(ConfigParameters.EXCEL_MIME_TYPES).toString().split(",");
                } catch (PatternSyntaxException e) {
                    SANDLogger.getLogger().error(
                            ConfigParameters.EXCEL_MIME_TYPES + " value is invalid");
                }
            } else {
                SANDLogger.getLogger().error(
                        ConfigParameters.EXCEL_MIME_TYPES + " can't be found");
            }

            // Excel Extensions Check
            if (mailProperties.get(ConfigParameters.EXCEL_EXTENSIONS) != null) {
                try {
                    ConfigParameters.EXCEL_EXTENSIONS_VALUE = mailProperties.get(ConfigParameters.EXCEL_EXTENSIONS).toString().split(",");
                } catch (PatternSyntaxException e) {
                    SANDLogger.getLogger().error(
                            ConfigParameters.EXCEL_EXTENSIONS + " value is invalid");
                }
            } else {
                SANDLogger.getLogger().error(
                        ConfigParameters.EXCEL_MIME_TYPES + " can't be found");
            }

            // POSCODE REGX Check
            if (mailProperties.get(ConfigParameters.POSCODE_REGX) != null) {
                try {
                    ConfigParameters.POSCODE_REGX_VALUE = mailProperties.get(
                            ConfigParameters.POSCODE_REGX).toString();
                } catch (PatternSyntaxException e) {
                    SANDLogger.getLogger().error(ConfigParameters.POSCODE_REGX + " value is invalid");
                }
            } else {
                SANDLogger.getLogger().error(
                        ConfigParameters.POSCODE_REGX + " can't be found");
            }

            // DIAL REGX Check
            if (mailProperties.get(ConfigParameters.DIAL_REGX) != null) {
                try {
                    ConfigParameters.DIAL_REGX_VALUE = mailProperties.get(
                            ConfigParameters.DIAL_REGX).toString();
                } catch (PatternSyntaxException e) {
                    SANDLogger.getLogger().error(
                            ConfigParameters.DIAL_REGX + " value is invalid");
                }
            } else {
                SANDLogger.getLogger().error(
                        ConfigParameters.DIAL_REGX + " can't be found");
            }

            // DIAL REGX Check
            if (mailProperties.get(ConfigParameters.SIM_REGX) != null) {
                try {
                    ConfigParameters.SIM_REGX_VALUE = mailProperties.get(
                            ConfigParameters.SIM_REGX).toString();
                } catch (PatternSyntaxException e) {
                    SANDLogger.getLogger().error(
                            ConfigParameters.SIM_REGX + " value is invalid");
                }
            } else {
                SANDLogger.getLogger().error(
                        ConfigParameters.SIM_REGX + " can't be found");
            }

            // CLEAN DATABASE
            if (mailProperties.get(ConfigParameters.CLEAN_DB) != null) {
                try {
                    ConfigParameters.CLEAN_DB_VALUE = Boolean.parseBoolean(mailProperties.get(
                            ConfigParameters.CLEAN_DB).toString());
                } catch (NumberFormatException e) {
                    SANDLogger.getLogger().error(
                            ConfigParameters.CLEAN_DB + " value is invalid");
                }
            } else {
                SANDLogger.getLogger().error(
                        ConfigParameters.CLEAN_DB + " can't be found");
            }

            // SEND REPLAY
            if (mailProperties.get(ConfigParameters.SEND_MAIL_REPLAY) != null) {
                try {
                    ConfigParameters.SEND_MAIL_REPLAY_VALUE = Boolean.parseBoolean(mailProperties.get(
                            ConfigParameters.SEND_MAIL_REPLAY).toString());
                } catch (NumberFormatException e) {
                    SANDLogger.getLogger().error(
                            ConfigParameters.SEND_MAIL_REPLAY + " value is invalid");
                }
            } else {
                SANDLogger.getLogger().error(
                        ConfigParameters.SEND_MAIL_REPLAY + " can't be found");
            }

            // CHECK POS
            if (mailProperties.get(ConfigParameters.CHECK_POS_AVAILABILITY) != null) {
                try {
                    ConfigParameters.CHECK_POS_AVAILABILITY_VALUE = Boolean.parseBoolean(mailProperties.get(
                            ConfigParameters.CHECK_POS_AVAILABILITY).toString());
                } catch (NumberFormatException e) {
                    SANDLogger.getLogger().error(
                            ConfigParameters.CHECK_POS_AVAILABILITY + " value is invalid");
                }
            } else {
                SANDLogger.getLogger().error(
                        ConfigParameters.CHECK_POS_AVAILABILITY + " can't be found");
            }

        } catch (IOException e) {
            SANDLogger.getLogger().error(
                    "ExchangeConnector.properties can't be found");
        }
        // Log4j File Check
        try {
            logProperties.load(ClassLoader.getSystemResourceAsStream("log4j.properties"));
        } catch (IOException e) {
            SANDLogger.getLogger().error("log4j.properties can't be found");
        }
       
        // Status_3 File Check
        if (ClassLoader.getSystemResourceAsStream("Status_3.txt") != null) {
            try {
                InputStreamReader isr = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("Status_3.txt"));
                BufferedReader br = new BufferedReader(isr);
                temp = line = "";
                while ((line = br.readLine()) != null) {
                    temp += (line + "\n");
                }
                ConfigParameters.STATUS_3 = temp;
                isr.close();
                br.close();
            } catch (IOException e) {
                SANDLogger.getLogger().error("Status_3.txt can't be read");
            }

        } else {
            SANDLogger.getLogger().error("Status_3.txt can't be found");
        }
        // Status_4 File Check
        if (ClassLoader.getSystemResourceAsStream("Status_4.txt") != null) {
            try {
                InputStreamReader isr = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("Status_4.txt"));
                BufferedReader br = new BufferedReader(isr);
                temp = line = "";
                while ((line = br.readLine()) != null) {
                    temp += (line + "\n");
                }
                ConfigParameters.STATUS_4 = temp;
                isr.close();
                br.close();
            } catch (IOException e) {
                SANDLogger.getLogger().error("Status_4.txt can't be read");
            }

        } else {
            SANDLogger.getLogger().error("Status_4.txt can't be found");
        }
        // Status_5 File Check
        if (ClassLoader.getSystemResourceAsStream("Status_5.txt") != null) {
            try {
                InputStreamReader isr = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("Status_5.txt"));
                BufferedReader br = new BufferedReader(isr);
                temp = line = "";
                while ((line = br.readLine()) != null) {
                    temp += (line + "\n");
                }
                ConfigParameters.STATUS_5 = temp;
                isr.close();
                br.close();
            } catch (IOException e) {
                SANDLogger.getLogger().error("Status_5.txt can't be read");
            }

        } else {
            SANDLogger.getLogger().error("Status_5.txt can't be found");
        }
        // Status_6 File Check
        if (ClassLoader.getSystemResourceAsStream("Status_6.txt") != null) {
            try {
                InputStreamReader isr = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("Status_6.txt"));
                BufferedReader br = new BufferedReader(isr);
                temp = line = "";
                while ((line = br.readLine()) != null) {
                    temp += (line + "\n");
                }
                ConfigParameters.STATUS_6 = temp;
                isr.close();
                br.close();
            } catch (IOException e) {
                SANDLogger.getLogger().error("Status_6.txt can't be read");
            }

        } else {
            SANDLogger.getLogger().error("Status_6.txt can't be found");
        }
        // Status_7 File Check
        if (ClassLoader.getSystemResourceAsStream("Status_7.txt") != null) {
            try {
                InputStreamReader isr = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("Status_7.txt"));
                BufferedReader br = new BufferedReader(isr);
                temp = line = "";
                while ((line = br.readLine()) != null) {
                    temp += (line + "\n");
                }
                ConfigParameters.STATUS_7 = temp;
                isr.close();
                br.close();
            } catch (IOException e) {
                SANDLogger.getLogger().error("Status_7.txt can't be read");
            }

        } else {
            SANDLogger.getLogger().error("Status_7.txt can't be found");
        }
        // Status_8 File Check
        if (ClassLoader.getSystemResourceAsStream("Status_8.txt") != null) {
            try {
                InputStreamReader isr = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("Status_8.txt"));
                BufferedReader br = new BufferedReader(isr);
                temp = line = "";
                while ((line = br.readLine()) != null) {
                    temp += (line + "\n");
                }
                ConfigParameters.STATUS_8 = temp;
                isr.close();
                br.close();
            } catch (IOException e) {
                SANDLogger.getLogger().error("Status_8.txt can't be read");
            }

        } else {
            SANDLogger.getLogger().error("Status_8.txt can't be found");
        }
        // Status_9 File Check
        if (ClassLoader.getSystemResourceAsStream("Status_9.txt") != null) {
            try {
                InputStreamReader isr = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("Status_9.txt"));
                BufferedReader br = new BufferedReader(isr);
                temp = line = "";
                while ((line = br.readLine()) != null) {
                    temp += (line + "\n");
                }
                ConfigParameters.STATUS_9 = temp;
                isr.close();
                br.close();
            } catch (IOException e) {
                SANDLogger.getLogger().error("Status_9.txt can't be read");
            }

        } else {
            SANDLogger.getLogger().error("Status_9.txt can't be found");
        }
        // Status_10 File Check
        if (ClassLoader.getSystemResourceAsStream("Status_10.txt") != null) {
            try {
                InputStreamReader isr = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("Status_10.txt"));
                BufferedReader br = new BufferedReader(isr);
                temp = line = "";
                while ((line = br.readLine()) != null) {
                    temp += (line + "\n");
                }
                ConfigParameters.STATUS_10 = temp;
                isr.close();
                br.close();
            } catch (IOException e) {
                SANDLogger.getLogger().error("Status_10.txt can't be read");
            }

        } else {
            SANDLogger.getLogger().error("Status_10.txt can't be found");
        }
        // Status_11 File Check
        if (ClassLoader.getSystemResourceAsStream("Status_11.txt") != null) {
            try {
                InputStreamReader isr = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("Status_11.txt"));
                BufferedReader br = new BufferedReader(isr);
                temp = line = "";
                while ((line = br.readLine()) != null) {
                    temp += (line + "\n");
                }
                ConfigParameters.STATUS_11 = temp;
                isr.close();
                br.close();
            } catch (IOException e) {
                SANDLogger.getLogger().error("Status_11.txt can't be read");
            }

        } else {
            SANDLogger.getLogger().error("Status_11.txt can't be found");
        }
        // Status_12 File Check
        if (ClassLoader.getSystemResourceAsStream("Status_12.txt") != null) {
            try {
                InputStreamReader isr = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("Status_12.txt"));
                BufferedReader br = new BufferedReader(isr);
                temp = line = "";
                while ((line = br.readLine()) != null) {
                    temp += (line + "\n");
                }
                ConfigParameters.STATUS_12 = temp;
                isr.close();
                br.close();
            } catch (IOException e) {
                SANDLogger.getLogger().error("Status_12.txt can't be read");
            }

        } else {
            SANDLogger.getLogger().error("Status_12.txt can't be found");
        }
        // Status_13 File Check
        if (ClassLoader.getSystemResourceAsStream("Status_13.txt") != null) {
            try {
                InputStreamReader isr = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("Status_13.txt"));
                BufferedReader br = new BufferedReader(isr);
                temp = line = "";
                while ((line = br.readLine()) != null) {
                    temp += (line + "\n");
                }
                ConfigParameters.STATUS_13 = temp;
                isr.close();
                br.close();
            } catch (IOException e) {
                SANDLogger.getLogger().error("Status_13.txt can't be read");
            }

        } else {
            SANDLogger.getLogger().error("Status_13.txt can't be found");
        }
        // Status_14 File Check
        if (ClassLoader.getSystemResourceAsStream("Status_14.txt") != null) {
            try {
                InputStreamReader isr = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("Status_14.txt"));
                BufferedReader br = new BufferedReader(isr);
                temp = line = "";
                while ((line = br.readLine()) != null) {
                    temp += (line + "\n");
                }
                ConfigParameters.STATUS_14 = temp;
                isr.close();
                br.close();
            } catch (IOException e) {
                SANDLogger.getLogger().error("Status_14.txt can't be read");
            }

        } else {
            SANDLogger.getLogger().error("Status_14.txt can't be found");
        }
        // Status_15 File Check
        if (ClassLoader.getSystemResourceAsStream("Status_15.txt") != null) {
            try {
                InputStreamReader isr = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("Status_15.txt"));
                BufferedReader br = new BufferedReader(isr);
                temp = line = "";
                while ((line = br.readLine()) != null) {
                    temp += (line + "\n");
                }
                ConfigParameters.STATUS_15 = temp;
                isr.close();
                br.close();
            } catch (IOException e) {
                SANDLogger.getLogger().error("Status_15.txt can't be read");
            }

        } else {
            SANDLogger.getLogger().error("Status_15.txt can't be found");
        }
        // Status_16 File Check
        if (ClassLoader.getSystemResourceAsStream("Status_16.txt") != null) {
            try {
                InputStreamReader isr = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("Status_16.txt"));
                BufferedReader br = new BufferedReader(isr);
                temp = line = "";
                while ((line = br.readLine()) != null) {
                    temp += (line + "\n");
                }
                ConfigParameters.STATUS_16 = temp;
                isr.close();
                br.close();
            } catch (IOException e) {
                SANDLogger.getLogger().error("Status_16.txt can't be read");
            }

        } else {
            SANDLogger.getLogger().error("Status_16.txt can't be found");
        }
        // Status_17 File Check
        if (ClassLoader.getSystemResourceAsStream("Status_17.txt") != null) {
            try {
                InputStreamReader isr = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("Status_17.txt"));
                BufferedReader br = new BufferedReader(isr);
                temp = line = "";
                while ((line = br.readLine()) != null) {
                    temp += (line + "\n");
                }
                ConfigParameters.STATUS_17 = temp;
                isr.close();
                br.close();
            } catch (IOException e) {
                SANDLogger.getLogger().error("Status_17.txt can't be read");
            }

        } else {
            SANDLogger.getLogger().error("Status_17.txt can't be found");
        }

        // Status_19 File Check
        if (ClassLoader.getSystemResourceAsStream("Status_19.txt") != null) {
            try {
                InputStreamReader isr = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("Status_19.txt"));
                BufferedReader br = new BufferedReader(isr);
                temp = line = "";
                while ((line = br.readLine()) != null) {
                    temp += (line + "\n");
                }
                ConfigParameters.STATUS_19 = temp;
                isr.close();
                br.close();
            } catch (IOException e) {
                SANDLogger.getLogger().error("Status_19.txt can't be read");
            }

        } else {
            SANDLogger.getLogger().error("Status_19.txt can't be found");
        }
    }
}
