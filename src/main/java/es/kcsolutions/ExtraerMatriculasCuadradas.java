package es.kcsolutions;

import java.io.File;
import java.io.FilenameFilter;


public class ExtraerMatriculasCuadradas {
	
	public static class DatosMatricula {
		
		public String matricula;
		public int probabilidad;
		public String x1,y1;
		public String x2,y2;
		public String x3,y3;
		public String x4,y4;
		
	}
	
	private static void createDir(String dir) {
		File dirFile = new File(dir);
		if (dirFile.exists() == false) {
			dirFile.mkdirs();
		}
	}
	
	public static void main(String[] args) {
		
		String sourceFolder = "d:/maquinas_virtuales/sharedFolder/matriculas_carmen/tmp2/";
		String imagenesMatriculasCuadradas = sourceFolder + "matriculasCuadradas/";
		String sinMatricula = sourceFolder + "sinMatriculas/";
		String probabilidad0 = sourceFolder + "probabilidad0/";
		String sinLetra = sourceFolder + "sinLetra/";
		String sinNumero = sourceFolder + "sinNumero/";
		
		int totalMatriculas = 0;
		
		if (sourceFolder.endsWith("/") == false) sourceFolder += "/";
		if (imagenesMatriculasCuadradas.endsWith("/") == false) imagenesMatriculasCuadradas += "/";
		if (sinMatricula.endsWith("/") == false) sinMatricula += "/";
		

		createDir(imagenesMatriculasCuadradas);
		createDir(sinMatricula);
		createDir(probabilidad0);
		createDir(sinLetra);
		createDir(sinNumero);
		
		
		
		
		File[] sourceFolderList = new File(sourceFolder).listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.contains(".jpg");
			}
		});
		
		for(File f: sourceFolderList) {
			System.out.println("Procesando..." + f.getName());
			
			if (f.getName().indexOf("()") != -1) {
				System.out.println("No hay matricula");
				f.delete();
				//f.renameTo(new File(sinMatricula + "/" + f.getName()));
				continue;
			}
			
			DatosMatricula datosMatricula = getDatos(f.getName());
			
			if (datosMatricula.probabilidad == 0) {
				System.out.println("La probabilidad es 0");
				f.delete();
				//f.renameTo(new File(probabilidad0 + "/" + f.getName()));
				continue;
			}
			
			if (datosMatricula.matricula.matches(".*\\d+.*") == false) {
				System.out.println("La matricula no tiene ningún número");
				f.delete();
				//f.renameTo(new File(sinNumero + "/" + f.getName()));
				continue;
			}
			
			if (datosMatricula.matricula.matches(".*[A-Za-z].*") == false) {
				System.out.println("La matricula no tiene ninguna letra");
				f.delete();
				//f.renameTo(new File(sinLetra + "/" + f.getName()));
				continue;
			}

			int plateWidth = new Integer(datosMatricula.x2).intValue() - new Integer(datosMatricula.x1).intValue();
			int plateHeight = new Integer(datosMatricula.y2).intValue() - new Integer(datosMatricula.y3).intValue();
			
			double ratio = Math.abs(plateWidth / plateHeight);
			if ((ratio > 0.9) && (ratio < 1.1)) {
				
				System.out.println("Moviendo " + f.getName() + " a " + imagenesMatriculasCuadradas);
				f.renameTo(new File(imagenesMatriculasCuadradas + "/" + f.getName()));
				totalMatriculas++;
			}

		}
		
		System.out.println("Total matriculas: " + totalMatriculas);
		System.out.println("=== FIN EJECUCION ===");
		
	}
	
	private static DatosMatricula getDatos(String nombre) {
		
		// 20180607152315836_3182981267955239471(9940JMT;66;274;116;371;112;372;131;275;135).jpg
		String datos = nombre.split("\\(")[1].substring(0, nombre.split("\\(")[1].length()-5);
		
		if(datos.endsWith(")")) {
			datos = datos.substring(0, datos.length()-1);
		}
		
		String tokens[] = datos.split(";");
		ExtraerMatriculasCuadradas.DatosMatricula datosMatricula = new ExtraerMatriculasCuadradas.DatosMatricula();
		datosMatricula.matricula = tokens[0];
		datosMatricula.probabilidad = new Integer(tokens[1]).intValue();
		
		datosMatricula.x1 = tokens[2];
		datosMatricula.y1 = tokens[3];
		
		datosMatricula.x2 = tokens[4];
		datosMatricula.y2 = tokens[5];
		
		datosMatricula.x3 = tokens[6];
		datosMatricula.y3 = tokens[7];
		
		datosMatricula.x4 = tokens[8];
		datosMatricula.y4 = tokens[9];
		
		return datosMatricula;
		
	}

}
