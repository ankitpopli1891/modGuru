package com.aakashapp.modguru.src;

public class Question {
	private String question;
    private Option[] options;
	private String note;
    
    public Question()
    {
    	options=new Option[4];
    }
 
    public String getQuestion() {
        return question;
    }
 
    public void setQuestion(String question) {
        this.question = question;
    }
    public void setOptions(String[] options,boolean isAnswer[]) {
    	for(int i=0;i<4;i++)
    	{
    		this.options[i]=new Option(options[i], isAnswer[i]);
    	}
	}
    
    public Option[] getOptions() {
		return options;
	}
    
	public void setNote(String note)
	{
		this.note=note;
	}
	
	public String getNote()
	{
		return this.note;
	}
    
    //Remove if not needed, used only for testing
    public String getDetails() {
        String result = question+ "\n " + options[0].getOptionValue()+ " "+options[0].isOptionCorrectAnswer()+ "\n" + options[1].getOptionValue()+ " "+options[1].isOptionCorrectAnswer() + "\n" + options[2].getOptionValue()+ " "+options[2].isOptionCorrectAnswer()
                + "\n" + options[3].getOptionValue()+ " "+options[3].isOptionCorrectAnswer()+"\n"+note;
        return result;
    }
}


