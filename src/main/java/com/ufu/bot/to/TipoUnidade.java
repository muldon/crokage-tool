package com.ufu.bot.to;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TipoUnidade{
	
	private Short id;
	private String descricao;
	
	public TipoUnidade(Short id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

	public TipoUnidade(TipoUnidadeEnum tipoUnidadeEnum) {
		this.id = tipoUnidadeEnum.getId();
		this.descricao = tipoUnidadeEnum.getDescricao();
	}
	

	public TipoUnidade() {
		
	}

	public Short getId() {
		return id;
	}
	public void setId(Short id) {
		this.id = id;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	@Override
	public String toString() {
		return "TipoUnidade [id=" + id + ", descricao=" + descricao + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TipoUnidade other = (TipoUnidade) obj;
		if (id != other.id)
			return false;
		return true;
	}
	

	public enum TipoUnidadeEnum {
		PRO_REITORIA (((Integer)1).shortValue(),"Pró-Reitoria"), 
	    DIRETORIA (((Integer)2).shortValue(),"Diretoria"),
	    DIVISAO (((Integer)3).shortValue(),"Divisao"),
	    SETOR (((Integer)4).shortValue(),"Setor"),
	    COORDENACAO(((Integer)5).shortValue(), "Coordenação");
		
	   
		private final Short id;
		private final String descricao;
		
		
		TipoUnidadeEnum(Short id,String descricao){
			this.id = id;
			this.descricao = descricao;
		
		}
		
		TipoUnidadeEnum(TipoUnidadeEnum tipoUnidade){
			this.id = tipoUnidade.id;
			this.descricao = tipoUnidade.descricao;
		}
		
	    public static TipoUnidadeEnum getTipoUnidade(Short id){
	    	switch(id){
	    		case(1): return PRO_REITORIA;
	    		case(2): return DIRETORIA;
	    		case(3): return DIVISAO;
	    		case(4): return SETOR;
	    		case(5): return COORDENACAO;
	    	}
	    	return null;
	    }
	
		public Short getId() {
			return id;
		}
	
		public String getDescricao() {
			return descricao;
		}

	
}

	
}



