package com.ubec.ubecapp.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.ubec.ubecapp.data.model.*

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // below is a sqlite query, where column names
        // along with their data types is given
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                PROJECT_COl + " TEXT," +
                LOCATION_COL + " TEXT," +
                LGA_COL + " TEXT," +
                QUALITY_COL + " TEXT," +
                COMPLETIONSTAGEID_COL + " INTEGER," +
                COMPLETIONSTAGE_COL + " TEXT," +
                COMPLETIONDESC_COL + " TEXT," +
                DEFECTSDESC_COL + " TEXT," +
                HASDEFECTS_COL + " TEXT," +
                TRANSACTIONID_COL + " TEXT," +
                COORDINATE_COL + " TEXT," +
                STATUS_COL + " TEXT," +
                INSPECTIONDATE_COL + " TEXT," +
                MODIFIEDBY_COL + " TEXT," +
                ATTACHMENT1_COL + " TEXT," +
                ATTACHMENT2_COL + " TEXT," +
                ATTACHMENT3_COL + " TEXT," +
                ATTACHMENT4_COL + " TEXT" + ")")

        val queryProject = ("CREATE TABLE " + PTABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                PROJECTID_COL + " TEXT," +
                SERIALNO_COL + " TEXT," +
                WORKFLOW_COL +" TEXT,"+
                PROJECTTYPE_COL +" TEXT,"+
                CONTRACTOR_COL + " TEXT," +
                DESCRIPTION_COL + " TEXT," +
                OWNEDBY_COL + " TEXT" + ")")

        val queryCodeList = ("CREATE TABLE " + CLTABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                CODE_COL + " INTEGER," +
                NAME_COL + " TEXT," +
                TYPE_COL + " TEXT" + ")")

        val queryMilestone = ("CREATE TABLE " + MILESTONETABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                PROJECTTYPE_COL + " TEXT," +
                PERCENTAGE_COL + " TEXT," +
                DESCRIPTION_COL + " TEXT" + ")")

        val querySupply = ("CREATE TABLE " + SUPPLYTABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                TRANSACTIONID_COL + " TEXT," +
                WORKFLOW_COL + " TEXT," +
                LGA_COL + " TEXT," +
                LOCATION_COL + " TEXT," +
                CONTRACTOR_COL + " TEXT," +
                VD_COL + " TEXT," +
                VO_COL + " TEXT," +
                REP_COL + " TEXT," +
                REPDESG_COL + " TEXT," +
                REPPHONE_COL + " TEXT," +
                MODIFIEDBY_COL + " TEXT," +
                COORDINATE_COL + " TEXT," +
                STATUS_COL + " TEXT," +
                ATTACHMENT1_COL + " TEXT," +
                ATTACHMENT2_COL + " TEXT," +
                ATTACHMENT3_COL + " TEXT," +
                ATTACHMENT4_COL + " TEXT" + ")")

        val querySupplyItem = ("CREATE TABLE " + SUPPLYITEMTABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                SUPPLYID_COL + " TEXT," +
                SERIALNO_COL + " TEXT," +
                DESCRIPTION_COL + " TEXT," +
                QORDER_COL + " TEXT," +
                QDELIVER_COL + " TEXT," +
                REMARK_COL + " TEXT," +
                MODIFIEDBY_COL + " TEXT" + ")")

        // we are calling sqlite
        // method for executing our query
        db.execSQL(query)
        db.execSQL(queryProject)
        db.execSQL(queryCodeList)
        db.execSQL(queryMilestone)
        db.execSQL(querySupply)
        db.execSQL(querySupplyItem)

    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $PTABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $CLTABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $MILESTONETABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $SUPPLYTABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $SUPPLYITEMTABLE_NAME")
        onCreate(db)
    }

    // This method is for adding data in our database
    fun addReport(model: ReportModel):Long{

        // below we are creating
        // a content values variable
        val values = ContentValues()
        // we are inserting our values
        // in the form of key-value pair
        values.put(PROJECT_COl, model.projectId)
        values.put(LOCATION_COL, model.location)
        values.put(LGA_COL, model.lga)
        values.put(QUALITY_COL, model.quality)
        values.put(COMPLETIONSTAGEID_COL,model.completionStageId)
        values.put(COMPLETIONSTAGE_COL, model.completionStage)
        values.put(COMPLETIONDESC_COL, model.completionDesc)
        values.put(HASDEFECTS_COL, model.hasDefects)
        values.put(DEFECTSDESC_COL, model.defectDesc)
        values.put(TRANSACTIONID_COL,model.transactionId)
        values.put(COORDINATE_COL,model.coordinate)
        values.put(STATUS_COL,model.status)
        values.put(INSPECTIONDATE_COL,model.inspectionDate)
        values.put(MODIFIEDBY_COL,model.modifiedBy)
        values.put(ATTACHMENT1_COL,model.attachment1)
        values.put(ATTACHMENT2_COL,model.attachment2)
        values.put(ATTACHMENT3_COL,model.attachment3)
        values.put(ATTACHMENT4_COL,model.attachment4)


        // here we are creating a
        // writable variable of
        // our database as we want to
        // insert value in our database
        val db = this.writableDatabase

        // all values are inserted into database
        var rtnId = db.insert(TABLE_NAME, null, values)

        // at last we are
        // closing our database
        db.close()
        return rtnId
    }
    fun addProject(model: ProjectModel){
        //check duplicate
        var existRow = this.getProjects2(model.projectId)
        if(existRow == null){
            val values = ContentValues()
            values.put(PROJECTID_COL, model.projectId)
            values.put(SERIALNO_COL,model.serialNo)
            values.put(WORKFLOW_COL,model.workflow)
            values.put(PROJECTTYPE_COL,model.projectType)
            values.put(CONTRACTOR_COL, model.contractor)
            values.put(DESCRIPTION_COL, model.description)
            values.put(OWNEDBY_COL, model.ownedBy)

            val db = this.writableDatabase

            db.insert(PTABLE_NAME, null, values)

            db.close()
        }
    }
    fun addCodeList(model: CodeListModel){
        val values = ContentValues()
        values.put(CODE_COL,model.code)
        values.put(NAME_COL,model.name)
        values.put(TYPE_COL,model.type)
        val db = this.writableDatabase
        db.insert(CLTABLE_NAME, null, values)
        db.close()
    }
    fun addMilestone(model: MilestoneModel){
        val values = ContentValues()
        values.put(PROJECTTYPE_COL,model.projectType)
        values.put(PERCENTAGE_COL,model.percentage)
        values.put(DESCRIPTION_COL,model.description)
        val db = this.writableDatabase
        db.insert(MILESTONETABLE_NAME, null, values)
        db.close()
    }
    fun addSupply(model: SupplyModel):Long{
        val values = ContentValues()
        values.put(TRANSACTIONID_COL,model.transactionId)
        values.put(WORKFLOW_COL,model.workflow)
        values.put(LGA_COL,model.lga)
        values.put(LOCATION_COL,model.location)
        values.put(CONTRACTOR_COL,model.contractor)
        values.put(COORDINATE_COL,model.coordinate)
        values.put(VD_COL,model.verificationDate)
        values.put(VO_COL,model.verificationOfficer)
        values.put(REP_COL,model.representative)
        values.put(REPDESG_COL,model.representativeDesg)
        values.put(REPPHONE_COL,model.representativePhone)
        values.put(MODIFIEDBY_COL,model.modifiedBy)
        values.put(STATUS_COL,model.status)
        values.put(ATTACHMENT1_COL,model.attachment1)
        values.put(ATTACHMENT2_COL,model.attachment2)
        values.put(ATTACHMENT3_COL,model.attachment3)
        values.put(ATTACHMENT4_COL,model.attachment4)
        val db = this.writableDatabase
        var id = db.insert(SUPPLYTABLE_NAME, null, values)
        db.close()
        return id
    }
    fun updateSupplyItem(model: SupplyItemModel):Long{
        val values = ContentValues()
        values.put(SUPPLYID_COL,model.supplyId)
        values.put(SERIALNO_COL,model.serialNo)
        values.put(DESCRIPTION_COL,model.description)
        values.put(QORDER_COL,model.quantityOrdered)
        values.put(QDELIVER_COL,model.quantityDelivered)
        values.put(REMARK_COL,model.remarks)
        values.put(MODIFIEDBY_COL,model.modifiedBy)
        val db = this.writableDatabase
        var resp = if(model.id != null){
            values.put(ID_COL,model.id)
            db.update(SUPPLYITEMTABLE_NAME, values, "id = ?", arrayOf(model.id))
            model.id!!.toLong()
        }else {
            db.insert(SUPPLYITEMTABLE_NAME, null, values)
        }
        db.close()
        return resp
    }
    //Update Methods
    fun updateReport(model: ReportModel):Boolean{
        // below we are creating
        // a content values variable
        val values = ContentValues()
        // we are inserting our values
        // in the form of key-value pair
        values.put(ID_COL, model.id)
        values.put(PROJECT_COl, model.projectId)
        values.put(LOCATION_COL, model.location)
        values.put(LGA_COL, model.lga)
        values.put(QUALITY_COL, model.quality)
        values.put(COMPLETIONSTAGEID_COL,model.completionStageId)
        values.put(COMPLETIONSTAGE_COL, model.completionStage)
        values.put(COMPLETIONDESC_COL, model.completionDesc)
        values.put(HASDEFECTS_COL, model.hasDefects)
        values.put(DEFECTSDESC_COL, model.defectDesc)
        values.put(TRANSACTIONID_COL,model.transactionId)
        values.put(COORDINATE_COL,model.coordinate)
        values.put(STATUS_COL,model.status)
        values.put(INSPECTIONDATE_COL,model.inspectionDate)
        values.put(MODIFIEDBY_COL,model.modifiedBy)
        values.put(ATTACHMENT1_COL,model.attachment1)
        values.put(ATTACHMENT2_COL,model.attachment2)
        values.put(ATTACHMENT3_COL,model.attachment3)
        values.put(ATTACHMENT4_COL,model.attachment4)


        // here we are creating a
        // writable variable
        val db = this.writableDatabase

        // all values are updated into database
        db.update(TABLE_NAME, values, "id = ?", arrayOf(model.id))

        // at last we are
        // closing our database
        db.close()
        return true
    }
    fun updateSupply(model: SupplyModel):Boolean{
        val values = ContentValues()

        values.put(ID_COL, model.id)
        values.put(TRANSACTIONID_COL,model.transactionId)
        values.put(WORKFLOW_COL,model.workflow)
        values.put(LGA_COL,model.lga)
        values.put(LOCATION_COL,model.location)
        values.put(CONTRACTOR_COL,model.contractor)
        values.put(COORDINATE_COL,model.coordinate)
        values.put(VD_COL,model.verificationDate)
        values.put(VO_COL,model.verificationOfficer)
        values.put(REP_COL,model.representative)
        values.put(REPDESG_COL,model.representativeDesg)
        values.put(REPPHONE_COL,model.representativePhone)
        values.put(MODIFIEDBY_COL,model.modifiedBy)
        values.put(STATUS_COL,model.status)
        values.put(ATTACHMENT1_COL,model.attachment1)
        values.put(ATTACHMENT2_COL,model.attachment2)
        values.put(ATTACHMENT3_COL,model.attachment3)
        values.put(ATTACHMENT4_COL,model.attachment4)
        val db = this.writableDatabase
        // all values are updated into database
        db.update(SUPPLYTABLE_NAME, values, "id = ?", arrayOf(model.id))
        // at last we are
        // closing our database
        db.close()
        return true
    }
    fun updateProject(model: ProjectModel){
            val values = ContentValues()
            //values.put(ID_COL, model.id)
            values.put(PROJECTID_COL, model.projectId)
            values.put(SERIALNO_COL,model.serialNo)
            values.put(WORKFLOW_COL,model.workflow)
            values.put(PROJECTTYPE_COL,model.projectType)
            values.put(CONTRACTOR_COL, model.contractor)
            values.put(DESCRIPTION_COL, model.description)
            values.put(OWNEDBY_COL, model.ownedBy)

            val db = this.writableDatabase

            db.update(PTABLE_NAME, values, "project_id = ?", arrayOf(model.projectId))

            db.close()
    }

    //Truncate Table
    fun reinitialiseProject(){
        val db = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $PTABLE_NAME")
        val queryProject = ("CREATE TABLE " + PTABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                PROJECTID_COL + " TEXT," +
                SERIALNO_COL + " TEXT," +
                WORKFLOW_COL +" TEXT,"+
                PROJECTTYPE_COL +" TEXT,"+
                CONTRACTOR_COL + " TEXT," +
                DESCRIPTION_COL + " TEXT," +
                OWNEDBY_COL + " TEXT" + ")")
        db.execSQL(queryProject)
        db.close()
    }


    // below method is to get
    // all data from our database
    fun getReports():List<ReportModel>{
        val reportList:ArrayList<ReportModel> = ArrayList<ReportModel>()
        val selectQuery = "SELECT  *,project_reports.id as mid,projects.id as pid FROM $TABLE_NAME INNER JOIN $PTABLE_NAME ON $PROJECTID_COL = $PROJECT_COl"
        if (getReport(selectQuery, reportList)) return ArrayList()
        return reportList
    }
    fun getReports(id:Int):ReportModel?{
        var report:ReportModel?=null
        val selectQuery = "SELECT  *,project_reports.id as mid,projects.id as pid FROM $TABLE_NAME INNER JOIN $PTABLE_NAME ON $PROJECTID_COL = $PROJECT_COl WHERE $TABLE_NAME.$ID_COL = $id"
        val reportList:ArrayList<ReportModel> = ArrayList<ReportModel>()
        if (getReport(selectQuery, reportList)) return null
        if(reportList.count() == 0) return null
        report = reportList.first()
        return report
    }
    fun getProjects():List<ProjectModel>{
        val projectList:ArrayList<ProjectModel> = ArrayList<ProjectModel>()
        val selectQuery = "SELECT  * FROM $PTABLE_NAME"
        if (getProject(selectQuery, projectList)) return ArrayList()
        return projectList
    }
    fun getProjects(serialNo:String?):ProjectModel?{
        var project:ProjectModel? = null
        val selectQuery = "SELECT  * FROM $PTABLE_NAME WHERE $SERIALNO_COL = '$serialNo'"
        val projectList:ArrayList<ProjectModel> = ArrayList<ProjectModel>()
        if (getProject(selectQuery, projectList)) return null
        if(projectList.count() == 0) return null
        project = projectList.first()
        return project
    }
    fun getProjects2(projectId:String?):ProjectModel?{
        var project:ProjectModel? = null
        val selectQuery = "SELECT  * FROM $PTABLE_NAME WHERE $PROJECTID_COL = $projectId"
        val projectList:ArrayList<ProjectModel> = ArrayList<ProjectModel>()
        if (getProject(selectQuery, projectList)) return null
        if(projectList.count() == 0) return null
        project = projectList.first()
        return project
    }

    fun getCodeList(type:String?):ArrayList<CodeListModel>{
        val clList:ArrayList<CodeListModel> = ArrayList<CodeListModel>()
        val selectQuery = "SELECT  * FROM $CLTABLE_NAME WHERE $TYPE_COL = '$type'"
        if (getCodeList(selectQuery, clList)) return ArrayList()
        return clList
    }
    fun getCodeListByName(name:String?):CodeListModel?{
        var cl:CodeListModel? = null
        val selectQuery = "SELECT  * FROM $CLTABLE_NAME WHERE $NAME_COL = '$name'"
        val clList:ArrayList<CodeListModel> = ArrayList<CodeListModel>()
        if (getCodeList(selectQuery, clList)) return null
        if(clList.count() == 0) return null
        cl = clList.first()
        return cl
    }

    fun getMilestones():List<MilestoneModel>{
        val mList:ArrayList<MilestoneModel> = ArrayList<MilestoneModel>()
        val selectQuery = "SELECT  * FROM $MILESTONETABLE_NAME"
        if (getMilestone(selectQuery, mList)) return ArrayList()
        return mList
    }
    fun getMilestones(projectType:String?):List<MilestoneModel>{
        val mList:ArrayList<MilestoneModel> = ArrayList<MilestoneModel>()
        val selectQuery = "SELECT  * FROM $MILESTONETABLE_NAME WHERE $PROJECTTYPE_COL = '$projectType'"
        if (getMilestone(selectQuery, mList)) return ArrayList()
        return mList
    }

    fun getSupplies():List<SupplyModel>{
        val sList:ArrayList<SupplyModel> = ArrayList<SupplyModel>()
        val selectQuery = "SELECT  * FROM $SUPPLYTABLE_NAME"
        if (getSupply(selectQuery, sList)) return ArrayList()
        return sList
    }
    fun getSupply(id:Int):SupplyModel?{
        var supply:SupplyModel? = null
        val sList:ArrayList<SupplyModel> = ArrayList<SupplyModel>()
        val selectQuery = "SELECT  * FROM $SUPPLYTABLE_NAME WHERE $ID_COL=$id"
        if (getSupply(selectQuery, sList)) return null
        if(sList.count()==0) return null
        supply = sList.first()
        return supply
    }
    fun getSupplyItems():List<SupplyItemModel>{
        val sList:ArrayList<SupplyItemModel> = ArrayList<SupplyItemModel>()
        val selectQuery = "SELECT  * FROM $SUPPLYITEMTABLE_NAME"
        if (getSupplyItem(selectQuery, sList)) return ArrayList()
        return sList
    }

    fun getSupplyItem(id:Int):SupplyItemModel?{
        var item:SupplyItemModel? = null
        val sList:ArrayList<SupplyItemModel> = ArrayList<SupplyItemModel>()
        val selectQuery = "SELECT  * FROM $SUPPLYITEMTABLE_NAME WHERE $ID_COL=$id"
        if (getSupplyItem(selectQuery, sList)) return null
        if(sList.count()==0) return null
        item = sList.first()
        return item
    }
    fun getSupplyItems(supplyId:String):List<SupplyItemModel>{
        val sList:ArrayList<SupplyItemModel> = ArrayList<SupplyItemModel>()
        val selectQuery = "SELECT  * FROM $SUPPLYITEMTABLE_NAME WHERE $SUPPLYID_COL = '$supplyId'"
        if (getSupplyItem(selectQuery, sList)) return ArrayList()
        return sList
    }

    //Delete Func
    fun deleteSupplyItem(id:String):Boolean{
        // on below line we are creating
        // a variable to write our database.
        val db = this.writableDatabase

        // on below line we are calling a method to delete our
        // course and we are comparing it with our course name.
        val resp = db.delete(SUPPLYITEMTABLE_NAME, "$ID_COL=?", arrayOf(id))
        db.close()
        return resp > 0
    }





    private fun getReport(
        selectQuery: String,
        reportList: ArrayList<ReportModel>,
    ): Boolean {
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            var error = e.message.toString()
            //db.execSQL(selectQuery)
            return true
        }
        var id: Int
        var pId: Int
        var projectId: String?
        var location: String?
        var lga: String?
        var quality: String?
        var completionStageId: Int
        var completionStage: String?
        var completionDesc: String?
        var hasDefects: String?
        var defectDesc: String?
        var transactionId: String?
        var coordinate: String?
        var status: String?
        var inspectionDate: String?
        var modifiedBy: String?
        var attachment1: String?
        var attachment2: String?
        var attachment3: String?
        var attachment4: String?

        var projectType: String?
        var contractor: String?
        var description: String?
        var serialNo: String?

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("mid"))
                pId = cursor.getInt(cursor.getColumnIndex("pid"))
                projectId = cursor.getString(cursor.getColumnIndex(PROJECT_COl))
                location = cursor.getString(cursor.getColumnIndex(LOCATION_COL))
                lga = cursor.getString(cursor.getColumnIndex(LGA_COL))
                quality = cursor.getString(cursor.getColumnIndex(QUALITY_COL))
                completionStageId = cursor.getInt(cursor.getColumnIndex(COMPLETIONSTAGEID_COL))
                completionStage = cursor.getString(cursor.getColumnIndex(COMPLETIONSTAGE_COL))
                completionDesc = cursor.getString(cursor.getColumnIndex(COMPLETIONDESC_COL))
                hasDefects = cursor.getString(cursor.getColumnIndex(HASDEFECTS_COL))
                defectDesc = cursor.getString(cursor.getColumnIndex(DEFECTSDESC_COL))
                transactionId = cursor.getString(cursor.getColumnIndex(TRANSACTIONID_COL))
                coordinate = cursor.getString(cursor.getColumnIndex(COORDINATE_COL))
                status = cursor.getString(cursor.getColumnIndex(STATUS_COL))
                modifiedBy = cursor.getString(cursor.getColumnIndex(MODIFIEDBY_COL))
                inspectionDate = cursor.getString(cursor.getColumnIndex(INSPECTIONDATE_COL))
                attachment1 = cursor.getString(cursor.getColumnIndex(ATTACHMENT1_COL))
                attachment2 = cursor.getString(cursor.getColumnIndex(ATTACHMENT2_COL))
                attachment3 = cursor.getString(cursor.getColumnIndex(ATTACHMENT3_COL))
                attachment4 = cursor.getString(cursor.getColumnIndex(ATTACHMENT4_COL))

                contractor = cursor.getString(cursor.getColumnIndex(CONTRACTOR_COL))
                description = cursor.getString(cursor.getColumnIndex(DESCRIPTION_COL))
                projectType = cursor.getString(cursor.getColumnIndex(PROJECTTYPE_COL))
                serialNo = cursor.getString(cursor.getColumnIndex(SERIALNO_COL))

                val report = ReportModel(id = id.toString(),
                    projectId = projectId,
                    location = location,
                    lga = lga,
                    quality = quality,
                    completionStageId = completionStageId,
                    completionStage = completionStage,
                    completionDesc = completionDesc,
                    hasDefects = hasDefects,
                    defectDesc = defectDesc,
                    transactionId = transactionId,
                    coordinate = coordinate,
                    status = status,
                    modifiedBy = modifiedBy,
                    inspectionDate = inspectionDate,
                    attachment1 = attachment1,
                    attachment2 = attachment2,
                    contractor = contractor,
                    description = description,
                    projectType = projectType,
                    serialNo = serialNo,
                    pId = pId.toString(),
                    attachment3 = attachment3,
                    attachment4 = attachment4)
                reportList.add(report)
            } while (cursor.moveToNext())
        }
        return false
    }
    private fun getMilestone(
        selectQuery: String,
        mList: ArrayList<MilestoneModel>,
    ): Boolean {
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return true
        }
        var id: Int
        var projectType: String?
        var percentage: String?
        var description: String?

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(ID_COL))
                percentage = cursor.getString(cursor.getColumnIndex(PERCENTAGE_COL))
                description = cursor.getString(cursor.getColumnIndex(DESCRIPTION_COL))
                projectType = cursor.getString(cursor.getColumnIndex(PROJECTTYPE_COL))


                val project = MilestoneModel(id = id.toString(),
                    description = description,
                    percentage = percentage,
                    projectType = projectType)
                mList.add(project)
            } while (cursor.moveToNext())
        }
        return false
    }

    private fun getSupply(
        selectQuery: String,
        sList: ArrayList<SupplyModel>,
    ): Boolean {
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return true
        }
        var id: Int
        var transactionId:String?
        var workflow:String?
        var location : String?
        var lga : String?
        var contractor : String?
        var coordinate : String?
        var verificationDate:String?
        var verificationOfficer:String?
        var representative:String?
        var representativeDesg:String?
        var representativePhone:String?
        var modifiedBy:String?
        var status:String?
        var attachment1: String?
        var attachment2: String?
        var attachment3: String?
        var attachment4: String?

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(ID_COL))
                transactionId = cursor.getString(cursor.getColumnIndex(TRANSACTIONID_COL))
                workflow = cursor.getString(cursor.getColumnIndex(WORKFLOW_COL))
                location = cursor.getString(cursor.getColumnIndex(LOCATION_COL))
                lga = cursor.getString(cursor.getColumnIndex(LGA_COL))
                contractor = cursor.getString(cursor.getColumnIndex(CONTRACTOR_COL))
                coordinate = cursor.getString(cursor.getColumnIndex(COORDINATE_COL))
                verificationDate = cursor.getString(cursor.getColumnIndex(VD_COL))
                verificationOfficer = cursor.getString(cursor.getColumnIndex(VO_COL))
                representative = cursor.getString(cursor.getColumnIndex(REP_COL))
                representativeDesg = cursor.getString(cursor.getColumnIndex(REPDESG_COL))
                representativePhone = cursor.getString(cursor.getColumnIndex(REPPHONE_COL))
                modifiedBy = cursor.getString(cursor.getColumnIndex(MODIFIEDBY_COL))
                status = cursor.getString(cursor.getColumnIndex(STATUS_COL))
                attachment1 = cursor.getString(cursor.getColumnIndex(ATTACHMENT1_COL))
                attachment2 = cursor.getString(cursor.getColumnIndex(ATTACHMENT2_COL))
                attachment3 = cursor.getString(cursor.getColumnIndex(ATTACHMENT3_COL))
                attachment4 = cursor.getString(cursor.getColumnIndex(ATTACHMENT4_COL))


                val supply = SupplyModel(id = id.toString(),
                    transactionId = transactionId,
                    workflow = workflow,
                    location = location,
                    lga = lga,
                    contractor = contractor,
                    coordinate = coordinate,
                    verificationDate = verificationDate,
                    verificationOfficer = verificationOfficer,
                    representative = representative,
                    representativeDesg = representativeDesg,
                    representativePhone = representativePhone,
                    modifiedBy = modifiedBy,
                    status = status,
                    attachment1 = attachment1,
                    attachment2 = attachment2,
                    attachment3 = attachment3,
                    attachment4 = attachment4)
                sList.add(supply)
            } while (cursor.moveToNext())
        }
        return false
    }

    private fun getSupplyItem(
        selectQuery: String,
        sList: ArrayList<SupplyItemModel>,
    ): Boolean {
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return true
        }
        var id: Int
        var supplyId:String?
        var serialNo:String?
        var description : String?
        var quantityOrdered:String?
        var quantityDelivered:String?
        var remarks:String?
        var modifiedBy:String?

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(ID_COL))
                supplyId = cursor.getString(cursor.getColumnIndex(SUPPLYID_COL))
                serialNo = cursor.getString(cursor.getColumnIndex(SERIALNO_COL))
                description = cursor.getString(cursor.getColumnIndex(DESCRIPTION_COL))
                quantityOrdered = cursor.getString(cursor.getColumnIndex(QORDER_COL))
                quantityDelivered = cursor.getString(cursor.getColumnIndex(QDELIVER_COL))
                remarks = cursor.getString(cursor.getColumnIndex(REMARK_COL))
                modifiedBy = cursor.getString(cursor.getColumnIndex(MODIFIEDBY_COL))


                val item = SupplyItemModel(id = id.toString(),
                    supplyId = supplyId,
                    serialNo = serialNo,
                    description = description,
                    quantityOrdered = quantityOrdered,
                    quantityDelivered = quantityDelivered,
                    remarks = remarks,
                    modifiedBy = modifiedBy)
                sList.add(item)
            } while (cursor.moveToNext())
        }
        return false
    }

    private fun getProject(
        selectQuery: String,
        projectList: ArrayList<ProjectModel>,
    ): Boolean {
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return true
        }
        var id: Int
        var projectId: String?
        var serialNo: String?
        var contractor: String?
        var description: String?
        var ownedBy: String?
        var workflow: String?
        var projectType: String?

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(ID_COL))
                projectId = cursor.getString(cursor.getColumnIndex(PROJECTID_COL))
                serialNo = cursor.getString(cursor.getColumnIndex(SERIALNO_COL))
                contractor = cursor.getString(cursor.getColumnIndex(CONTRACTOR_COL))
                description = cursor.getString(cursor.getColumnIndex(CONTRACTOR_COL))
                ownedBy = cursor.getString(cursor.getColumnIndex(OWNEDBY_COL))
                workflow = cursor.getString(cursor.getColumnIndex(WORKFLOW_COL))
                projectType = cursor.getString(cursor.getColumnIndex(PROJECTTYPE_COL))


                val project = ProjectModel(id = id.toString(),
                    projectId = projectId,
                    serialNo = serialNo,
                    contractor = contractor,
                    description = description,
                    ownedBy = ownedBy,
                    projectType = projectType,workflow = workflow)
                projectList.add(project)
            } while (cursor.moveToNext())
        }
        return false
    }

    private fun getCodeList(
        selectQuery: String,
        clList: ArrayList<CodeListModel>,
    ): Boolean {
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return true
        }
        var id: Int
        var code: String?
        var name: String?
        var type: String?
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(ID_COL))
                code = cursor.getString(cursor.getColumnIndex(CODE_COL))
                name = cursor.getString(cursor.getColumnIndex(NAME_COL))
                type = cursor.getString(cursor.getColumnIndex(TYPE_COL))
                val row = CodeListModel(id = id.toString(), code = code, name = name, type = type)
                clList.add(row)
            } while (cursor.moveToNext())
        }
        return false
    }

    companion object{
        // here we have defined variables for our database

        // below is variable for database name
        private val DATABASE_NAME = "ubecdb"

        // below is the variable for database version
        private val DATABASE_VERSION = 1

        // below is the variable for table name
        val TABLE_NAME = "project_reports"

        // below is the variable for id column
        val ID_COL = "id"
        // below is the variable for transactionId column
        val TRANSACTIONID_COL = "transaction_id"

        // below is the variable for project column
        val PROJECT_COl = "project"

        // below is the variable for location column
        val LOCATION_COL = "location"

        // below is the variable for lga column
        val LGA_COL = "lga"

        // below is the variable for quality column
        val QUALITY_COL = "quality"

        // below is the variable for completion_stage column
        val COMPLETIONSTAGE_COL = "completion_stage"
        val COMPLETIONSTAGEID_COL = "completion_stage_id"

        // below is the variable for completion_desc column
        val COMPLETIONDESC_COL = "completion_desc"

        // below is the variable for has_defects column
        val HASDEFECTS_COL = "has_defects"

        // below is the variable for defects_desc column
        val DEFECTSDESC_COL = "defects_desc"

        // below is the variable for Coordinate column
        val COORDINATE_COL = "coordinate"
        // below is the variable for Status column
        val STATUS_COL = "status"
        // below is the variable for ModifiedBy column
        val MODIFIEDBY_COL = "modified_by"
        // below is the variable for ModifiedBy column
        val INSPECTIONDATE_COL = "inspection_date"
        // below is the variable for image1 path column
        val ATTACHMENT1_COL = "image1"
        // below is the variable for image2 path column
        val ATTACHMENT2_COL = "image2"
        // below is the variable for image3 path column
        val ATTACHMENT3_COL = "image3"
        // below is the variable for image4 path column
        val ATTACHMENT4_COL = "image4"


        //CodeList

        // below is the variable for table name
        val CLTABLE_NAME = "codelist"
        val CODE_COL = "code"
        val NAME_COL = "name"
        val TYPE_COL = "type"

        //Projects
        val PTABLE_NAME = "projects"
        val PROJECTID_COL = "project_id"
        val SERIALNO_COL = "serial_no"
        val WORKFLOW_COL = "workflow"
        val PROJECTTYPE_COL = "project_type"
        val CONTRACTOR_COL = "contractor"
        val DESCRIPTION_COL = "description"
        val OWNEDBY_COL = "owned_by"

        //Milestones
        val MILESTONETABLE_NAME = "milestones"
        val PERCENTAGE_COL = "percentage"

        //Supply
        val SUPPLYTABLE_NAME = "supplies"
        val SUPPLYITEMTABLE_NAME = "supply_items"
        val VD_COL = "verification_date"
        val VO_COL = "verification_officer"
        val REP_COL = "representative"
        val REPDESG_COL = "representative_desg"
        val REPPHONE_COL = "representative_phone"
        val QORDER_COL = "quantity_ordered"
        val QDELIVER_COL = "quantity_delivered"
        val REMARK_COL = "remarks"
        val SUPPLYID_COL = "supply_id"


    }
}