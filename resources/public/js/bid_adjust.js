function bid_adjust(campaignName) {
  var maxCPC = 0;
  var ignored = new RegExp(/word1|word2/);
  
  //set bid increments
  var drop = function(b,x) {
    return b - (increment(1) * x);
  }

  var increment = function(i) {
    if(maxCPC > 10)        { return 0.50 * i; } 
    else if(maxCPC > 8)    { return 0.25 * i; }
    else if(maxCPC > 4.00) { return 0.15 * i; }
    else if(maxCPC > 2.00) { return 0.10 * i; }  
    else                   { return 0.05 * i; }    
  }  

  var keywordIterator = AdWordsApp.keywords()
      .withCondition("CampaignName = '" + campaignName + "'")
      .withCondition("Status = ENABLED")
      .get();        
  
  while(keywordIterator.hasNext()) {
    var keyword = keywordIterator.next();
    var bid = keyword.getFirstPageCpc();
    var max = keyword.getMaxCpc();
    var text = keyword.getText();
    if ((bid < max) && !text.match(ignored)) {
      keyword.setMaxCpc(bid + increment(1));
    }
  }
}

function main () {
    var campaigns = ["Coastal Inkjets Recycling", "Cell Phone Recycling", "Computer Recycling", "CRT Monitor Recycling", "E-Waste Recycling",
		     "Inkjet Cartridge Recycling", "Laptop Recycling", "Laser Printer Cartridge Recycling", "Smartphone and Tablet recycling",
		     "Surplus Toner Recycling"];
    for (var i = 0; i < campaigns.length; i++) {
	bid_adjust(campaigns[i]);
    }
}
