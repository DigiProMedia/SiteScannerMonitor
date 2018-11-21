package com.digipro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmdUtils {

	private static Logger logger = LoggerFactory.getLogger(CmdUtils.class);

	public static CommandResult processCmd(String cmd, boolean wait, boolean printOutput) throws IOException {
		CommandResult cmdResult = new CommandResult();

		Process p = Runtime.getRuntime().exec(cmd);

		try {

			processStandardOut(p, printOutput, cmdResult);
			if (wait) {
				int result = 0;
				result = p.waitFor();

				cmdResult.setResult(result);
			}

			return cmdResult;

		} catch (InterruptedException ie) {
			throw new IOException(ie);
		}
	}

	public static void processStandardOut(Process p, boolean printOutput, CommandResult cmdResult) {
		BufferedReader stdInput = null;
		BufferedReader stdError = null;
		StringBuilder output = new StringBuilder();

		try {
			try {
				stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
				stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String s = null;
				int percent = 0;

				while ((s = stdInput.readLine()) != null) {
					if (printOutput) {
						System.err.println(s);
					}

					output.append(s);
				}

				while ((s = stdError.readLine()) != null) {
					if (printOutput)
						System.err.println(s);

				}

				cmdResult.setResponse(output.toString());

			} catch (Exception e) {
				logger.error("processStandardOut", e);
			} finally {
				stdInput.close();
			}
		} catch (Exception e) {
			logger.error("processStandardOut", e);
		}

	}

	public static List<String> getStandardOutList(Process p) {
		BufferedReader stdInput = null;
		BufferedReader stdError = null;
		List<String> output = new ArrayList<String>();
		try {
			try {
				stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
				stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String s = null;

				while ((s = stdInput.readLine()) != null) {
					output.add(s);
				}
				while ((s = stdError.readLine()) != null) {
					System.err.println(s);
				}
			} catch (Exception e) {
				logger.error("processStandardOut", e);
			} finally {
				stdInput.close();
			}
		} catch (Exception e) {
			logger.error("processStandardOut", e);
		}

		return output;
	}

	public static String getStandardOut(Process p) {
		BufferedReader stdInput = null;
		BufferedReader stdError = null;
		StringBuilder output = new StringBuilder();

		try {
			try {
				stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
				stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String s = null;

				while ((s = stdInput.readLine()) != null) {
					output.append(s);
				}
				while ((s = stdError.readLine()) != null) {
					System.err.println(s);
				}
			} catch (Exception e) {
				logger.error("processStandardOut", e);
			} finally {
				stdInput.close();
			}
		} catch (Exception e) {
			logger.error("processStandardOut", e);
		}

		return output.toString();
	}
}
