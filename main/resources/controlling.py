import requests
import pyodata

from datetime import datetime

SERVICE_URL = "https://api.logmytime.de/V1/Api.svc"
session = requests.Session()
session.auth = ("X-LogMyTimeApiKey", "")
# Create instance of OData client
logmytime = pyodata.Client(SERVICE_URL, session)



def printTimeEntry(timeEntry):
    print(timeEntry.ID, timeEntry.UserID, timeEntry.ProjectID, timeEntry.TaskID, timeEntry.Comment)

def printUser(user):
    print(user.ID, user.FirstName, user.LastName)


def groupAllByTimeEntry(timeEntries, users, projects, tasks):
    
    resDict = []
    for timeEntry in timeEntries:
        userID = timeEntry.UserID
        projectID = timeEntry.ProjectID
        taskID = timeEntry.TaskID

        resUser = ""
        for user in users:
            if (user.ID == userID):
                resUser = user

        resProject = ""
        for project in projects:
            if(project.ID == projectID):
                resProject = project
        
        resTask = ""
        for task in tasks:
            if (task.ID == taskID):
                resTask = task

        resDict.append({'timeEntry' : timeEntry,
                        'user' : resUser,
                        'project' : resProject,
                        'task' : resTask
                        })
    return resDict

def filterResListByUser(userID, resList):
    res = []
    for x in resList:
        if (x['user'].ID == userID):
            res.append(x)
    return res

def filterResListByDate(resList, year=None, month=None, day=None):
    # TODO: Write this
    pass


def getAllTimeEntries():
    TimeEntries = []
    timeEntriesCount = logmytime.entity_sets.TimeEntries.get_entities().count().execute()
    for x in range(0,timeEntriesCount, 1000):
        TimeEntries.extend(logmytime.entity_sets.TimeEntries.get_entities().skip(x).execute())
    return TimeEntries

def filterTimeEntriesByMonth(month, timeEntries):
    res = []
    for t in timeEntries:
        startTime = t.StartTime
        endTime = t.EndTime
        if (startTime.month == month and endTime.month == month):
            res.append(t)
    return res

def filterTimeEntriesByYear(year, timeEntries):
    res = []
    for t in timeEntries:
        startTime = t.StartTime
        endTime = t.EndTime
        if (startTime.year == year and endTime.year == year):
            res.append(t)
    return res

# Getting all the data
TimeEntries = getAllTimeEntries()
Users = logmytime.entity_sets.Users.get_entities().execute()
Projects = logmytime.entity_sets.Projects.get_entities().execute()
Tasks = logmytime.entity_sets.Tasks.get_entities().execute()

combinedEntitiesList = groupAllByTimeEntry(TimeEntries, Users, Projects, Tasks)
