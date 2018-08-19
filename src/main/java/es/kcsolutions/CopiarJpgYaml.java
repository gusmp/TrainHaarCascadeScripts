package es.kcsolutions;

import java.io.File;
import java.io.FilenameFilter;

public class CopiarJpgYaml {

	public static void main(String[] args) {

		//String sourceFolder = "d:/maquinas_virtuales/sharedFolder/matriculas_carmen/07 fernando 1/";
		//String sourceFolder = "d:/maquinas_virtuales/sharedFolder/matriculas_carmen/09_copias_matriculas_enviades_anpr/";
		String sourceFolder = "d:/maquinas_virtuales/sharedFolder/matriculas_carmen/09_copias_matriculas_enviades_anpr/";
		String targetFolder = "d:/maquinas_virtuales/sharedFolder/matriculas_carmen/02_pack02/";

		int movimientosTotal = 0;
		
		if (sourceFolder.endsWith("/") == false) sourceFolder += "/";
		if (targetFolder.endsWith("/") == false) targetFolder += "/";

		File[] sourceFolderList = new File(sourceFolder).listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.contains(".yaml");
			}
		});

		for (File f : sourceFolderList) {
			System.out.println("Procesando..." + f.getName());

			String tokens[] = f.getName().split("\\)-");
			if (tokens.length == 2) {
				String img = tokens[0] + ").jpg";
				File imgFile = new File(f.getParent() + "/" + img);
				if (imgFile.exists() == true) {
					System.out.println("Moviendo " + f.getAbsolutePath() + " -> " + targetFolder);
					f.renameTo(new File(targetFolder+ "/" + f.getName()));
					System.out.println("Moviendo " + imgFile.getAbsolutePath() + " -> " + targetFolder);
					imgFile.renameTo(new File(targetFolder + "/" + imgFile.getName()));
					movimientosTotal++;
				}
			}
		}

		System.out.println("Total movimientos(yaml+jpg): " + movimientosTotal);

	}

}
