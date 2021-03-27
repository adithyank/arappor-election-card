var constituencyList = ['GUMMIDIPOONDI', 'ALANDUR', 'PONNERI','TIRUTTANI','THIRUVALLUR','POONMALLAE','AVADI','MADURAVOYAL','AMBATTUR','MADAVARAM','THIRUVOTTIYUR','DR_RADHAKRISHNAN_NAGAR','PERAMBUR','KOLATHUR','VILLIVAKKAM','THIRU_VI_KA_NAGAR','EGMORE','ROYAPURAM','HARBOUR','CHEPAUK_THIRUVALLIKENI','THOUSAND_LIGHTS','ANNA_NAGAR','VIRUGAMPAKKAM','SAIDAPET','THIYAGARAYANAGAR','MYLAPORE','VELACHERY','SHOZHINGANALLUR','ALANDUR','SRIPERUMBUDUR','PALLAVARAM','TAMBARAM','CHENGALPATTU','THIRUPORUR','CHEYYUR','MADURANTAKAM','UTHIRAMERUR','KANCHEEPURAM','ARAKKONAM','SHOLINGUR','KATPADI','RANIPET','ARCOT','VELLORE','ANAIKATTU','KILVAITHINANKUPPAM','GUDIYATTAM','VANIYAMBADI','AMBUR','JOLARPET','TIRUPATTUR','UTHANGARAI','BARGUR','KRISHNAGIRI','VEPPANAHALLI','HOSUR','THALLI','PALACODU','PENNAGARAM','DHARMAPURI','PAPPIREDDIPPATTI','HARUR','CHENGAM','TIRUVANNAMALAI','KILPENNATHUR','KALASAPAKKAM','POLUR','ARANI','CHEYYAR','VANDAVASI','GINGEE','MAILAM','TINDIVANAM','VANUR','VILLUPURAM','VIKRAVANDI','TIRUKKOYILUR','ULUNDURPETTAI','RISHIVANDIYAM','SANKARAPURAM','KALLAKURICHI','GANGAVALLI','ATTUR','YERCAUD','OMALUR','METTUR','EDAPPADI','SANKARI','SALEM_WEST','SALEM_NORTH','SALEM_SOUTH','VEERAPANDI','RASIPURAM','SENTHAMANGALAM','NAMAKKAL','PARAMATHI_VELUR','TIRUCHENGODU','KUMARAPALAYAM','ERODE_EAST','ERODE_WEST','MODAKKURICHI','DHARAPURAM','KANGAYAM','PERUNDURAI','BHAVANI','ANTHIYUR','GOBICHETTIPALAYAM','BHAVANISAGAR','UDHAGAMANDALAM','GUDALUR','COONOOR','METTUPPALAYAM','AVANASHI','TIRUPPUR_NORTH','TIRUPPUR_SOUTH','PALLADAM','SULUR','KAVUNDAMPALAYAM','COIMBATORE_NORTH','THONDAMUTHUR','COIMBATORE_SOUTH','SINGANALLUR','KINATHUKADAVU','POLLACHI','VALPARAI','UDUMALAIPETTAI','MADATHUKULAM','PALANI','ODDANCHATRAM','ATHOOR','NILAKKOTTAI','NATHAM','DINDIGUL','VEDASANDUR','ARAVAKURICHI','KARUR','KRISHNARAYAPURAM','KULITHALAI','MANAPPARAI','SRIRANGAM','TIRUCHIRAPPALLI_WEST','TIRUCHIRAPPALLI_EAST','THIRUVERUMBUR','LALGUDI','MANACHANALLUR','MUSIRI','THURAIYUR','PERAMBALUR','KUNNAM','ARIYALUR','JAYANKONDAM','TITTAKUDI','VRIDDHACHALAM','NEYVELI','PANRUTI','CUDDALORE','KURINJIPADI','BHUVANAGIRI','CHIDAMBARAM','KATTUMANNARKOIL','SIRKAZHI','MAYILADUTHURAI','POOMPUHAR','NAGAPATTINAM','KILVELUR','VEDARANYAM','THIRUTHURAIPOONDI','MANNARGUDI','THIRUVARUR','NANNILAM','THIRUVIDAIMARUDUR','KUMBAKONAM','PAPANASAM','THIRUVAIYARU','THANJAVUR','ORATHANADU','PATTUKKOTTAI','PERAVURANI','GANDHARVAKOTTAI','VIRALIMALAI','PUDUKKOTTAI','THIRUMAYAM','ALANGUDI','ARANTHANGI','KARAIKUDI','TIRUPPATTUR','SIVAGANGA','MANAMADURAI','MELUR','MADURAI_EAST','SHOLAVANDAN','MADURAI_NORTH','MADURAI_SOUTH','MADURAI_CENTRAL','MADURAI_WEST','THIRUPARANKUNDRAM','THIRUMANGALAM','USILAMPATTI','ANDIPATTI','PERIYAKULAM','BODINAYAKANUR','CUMBUM','RAJAPALAYAM','SRIVILLIPUTHUR','SATTUR','SIVAKASI','VIRUDHUNAGAR','ARUPPUKKOTTAI','TIRUCHULI','PARAMAKUDI','TIRUVADANAI','RAMANATHAPURAM','MUDHUKULATHUR','VILATHIKULAM','THOOTHUKKUDI','TIRUCHENDUR','SRIVAIKUNTAM','OTTAPIDARAM','KOVILPATTI','SANKARANKOVIL','VASUDEVANALLUR','KADAYANALLUR','TENKASI','ALANGULAM','TIRUNELVELI','AMBASAMUDRAM','PALAYAMKOTTAI','NANGUNERI','RADHAPURAM','KANNIYAKUMARI','NAGERCOIL','COLACHAL','PADMANABHAPURAM','VILAVANCODE','KILLIYOOR']
var constituencyList1 = ['SIRKAZHI']
var constituencyBaseUrl = 'http://election.arappor.org/api/candidates-by-constituency/CONSTITUENCY'
var constituencyBaseUrlSO = 'assets/data/clientjson/CONSTITUENCY.json'

var candidatesTableConfig = {

        table: {
                name: 'candidatesTable',

                show: {
                    toolbar          : true,
                },

                columns: [
                    { field: 'name', text: 'Name', size: '40%', sortable: true, searchable: true },
                    { field: 'partyName', text: 'Party', size: '60%', sortable: true, searchable: true }
                ],

                onClick: function(event) {
                    var grid = this;
                    //var form = w2ui.subjectAdditionForm;

                    event.onComplete = function () {
                        var sel = grid.getSelection();

                        if (sel.length == 0) {return}

                        var tableData = grid.get(sel[0])

                        sirpi.jsrender.loadContent("videocardcontentdiv", "assets/templates/vcdesign.html", tableData)
                    }
                }
            }
        }

var getConstituencyJson = function(constituencyName, obj)
{
    var url = constituencyBaseUrlSO.replace("CONSTITUENCY", constituencyName)
    console.log('url = ', url)

    $.ajax({
        mimeType: 'text/html; charset=utf-8',
        url: url,
        type: 'GET',
        success: function(data) {
           obj.success(data)
        },
        dataType: "html",
        async: false
    });
}

$(document).on('click', '#btnLoadConstituency', function(e) {
    var sel = $('#constituencyList').w2field().get().id

    getConstituencyJson(sel, {
        success: function(r) {

            var c = JSON.parse(r)
            window.currentTableJSON = c.candidates

            //console.log("resp", c)
            //w2ui.candidatesTable.destroy()

            w2ui[candidatesTableConfig.table.name].records = c.candidates

            //$('#tablediv').w2grid(candidatesTableConfig.table)
            w2ui[candidatesTableConfig.table.name].refresh()

            if (c.candidates[0])
                w2ui.candidatesTable.click(c.candidates[0].recid)

//            w2ui[candidatesTableConfig.table.name].load('assets/json/cand.json', function() {
//                w2ui.candidatesTable.click(w2ui.candidatesTable.records[0].recid)
//            })
            //showCandidate(JSON.parse(r))
        }
    })

})

var saveAllVideoCards = function()
{
//    alert("wait, i m coming")
    let constituencyIdx = 0
    let previousConstituencyIdx = -1

    let candidateTableIdx = 0

    var job = setInterval(function(){
        if (window.stop == 1)
        {
            console.log("stopping job")
            clearInterval(job)
        }

        let c = constituencyList[constituencyIdx]

        let progress = "Progress : Constituency: " + constituencyIdx + " : " + c + ". candidate index : " + candidateTableIdx;
        console.log(progress)
        $('#labelProgress').html(progress)

        if (previousConstituencyIdx != constituencyIdx)
        {
            $('#constituencyList').w2field().set({id: c, text: c})
            $('#btnLoadConstituency').click()
        }

        previousConstituencyIdx = constituencyIdx

        if (window.currentTableJSON.length == 0)
        {
            if (constituencyIdx == constituencyList.length - 1)
            {
                console.log("stopping job")
                clearInterval(job)
            }

            constituencyIdx++
            candidateTableIdx = 0
            return
        }

        let candidate = window.currentTableJSON[candidateTableIdx]
        w2ui.candidatesTable.click(candidate.recid)
        saveDivAsImage(c, candidate.photoName)

        if (candidateTableIdx == window.currentTableJSON.length - 1)
        {
            if (constituencyIdx == constituencyList.length - 1)
            {
                console.log("stopping job")
                clearInterval(job)
            }

            constituencyIdx++
            candidateTableIdx = 0
        }
        else
        {
            candidateTableIdx++
        }
        console.log("done..")
    }, 500)

}

var showCandidate = async function(obj)
{
    console.log("obj = ", obj)

    var c = obj.data[0]

    sirpi.jsrender.loadContent("videocardcontentdiv", "assets/templates/vcdesign.html", o)
    //await sirpi.sleep(1000)
    await new Promise(r => setTimeout(r, 2000));

    saveDivAsImage2()
}

var printJson = function()
{
    for (cname in constituencyList1)
    {
        getConstituencyJson(constituencyList1[cname], {
            success: function(r) {
                showCandidate(JSON.parse(r))
            }
        })
    }
}


$(document).ready(function() {
    //printJson()
    $('#constituencyList').w2field('list', {items: constituencyList, match: 'contains'})
    $('#constituencyList').w2field().set({id: constituencyList[0], text: constituencyList[0]})

    $('#tablediv').w2grid(candidatesTableConfig.table)
    $('#btnLoadConstituency').click()
})
