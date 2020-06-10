import requests
import pyodata
import sys

from datetime import datetime


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

        resDict.append({'timeEntry': timeEntry,
                        'user': resUser,
                        'project': resProject,
                        'task': resTask
                        })
    return resDict

def getAllTimeEntries(logmytime):
    TimeEntries = []
    timeEntriesCount = logmytime.entity_sets.TimeEntries.get_entities().count().execute()
    # TODO: Make this more robust and check result
    for x in range(0, timeEntriesCount, 1000):
        TimeEntries.extend(
            logmytime.entity_sets.TimeEntries.get_entities().skip(x).execute())
    return TimeEntries


##
# Functions that work with the resList:
##
def filterResListByUser(userID, resList):
    res = []
    for x in resList:
        if (x['user'].ID == userID):
            res.append(x)
    return res

def filterResListStartTimeByDay(resList, day):
    filteredResList = []
    for x in resList:
        if (x['timeEntry'].StartTime.day == day):
            filteredResList.append(x)
    return filteredResList

def filterResListStartTimeByMonth(resList, month):
    filteredResList = []
    for x in resList:
        if (x['timeEntry'].StartTime.month == month):
            filteredResList.append(x)
    return filteredResList

def filterResListStartTimeByYear(resList, year):
    filteredResList = []
    for x in resList:
        if (x['timeEntry'].StartTime.year == year):
            filteredResList.append(x)
    return filteredResList

def filterResListByDate(resList, year=None, month=None, day=None):
    filteredResList = []
    if year is not None:
        filteredResList = filterResListStartTimeByYear(resList, year)
    if month is not None:
        filteredResList = filterResListStartTimeByMonth(filteredResList, month)
    if day is not None:
        filteredResList = filterResListStartTimeByDay(filteredResList, day)
    return filteredResList

def filterByProject(resList, projectID):
    filteredResList = []
    for x in resList:
        if (x['project'].ID == projectID):
            filteredResList.append(x)
    return filteredResList

def sumTimeEntriesByProjectInHours(resList, projectID):
    filteredByProject = filterByProject(resList, projectID)
    sumInSeconds = 0
    for x in filteredByProject:
        sumInSeconds = sumInSeconds + x['timeEntry'].DurationSeconds
    return (sumInSeconds / 60 / 60)

def sumOfTimeEntriesInResListInHours(resList):
    sum = 0
    for x in resList:
        duration = x['timeEntry'].DurationSeconds
        sum = sum + duration
    return (sum / 60 / 60)

def getSumOfTimeEntriesOfAllProjectsInHours(resList):
    res = {}
    for x in resList:
        project = x['project']
        if not(project.ID in res):
            res[project.ID] = {
                'name': project.Name, 'hours': sumTimeEntriesByProjectInHours(resList, project.ID)}
    return res

def getSumOfEntriesByAllProjectsAndTasks(resList):
    # TODO: Write
    pass


##
# Output related functions and pretty printing
##
def printTimeEntry(timeEntry):
    print(timeEntry.ID, timeEntry.UserID, timeEntry.ProjectID,
          timeEntry.TaskID, timeEntry.Comment)

def printUser(user):
    print(user.ID, user.FirstName, user.LastName)

def printProject(project):
    print(project.ID, project.Name)

def printTimeEntriesInResList(resList):
    for x in resList:
        timeEntry = x['timeEntry']
        printTimeEntry(timeEntry)

def printProjectsInResList(resList):
    for x in resList:
        project = x['project']
        printProject(project)

def printProjectHours(dictWithProjectHours):
    print("{:<8} {:<45} {:<10}".format('ID', 'Name', 'Hours'))
    for k, v in dictWithProjectHours.items():
        name, hours = v['name'], v['hours']
        print("{:<8} {:<45} {:<10}".format(k, name, hours))


if __name__ == "__main__":
    
    if len(sys.argv) == 1:
        print("some doc")

    elif sys.argv[1] == "controlling":
        SERVICE_URL = "https://api.logmytime.de/V1/Api.svc"
        session = requests.Session()
        session.auth = ("X-LogMyTimeApiKey", sys.argv[2])
        # Create instance of OData client
        logmytime = pyodata.Client(SERVICE_URL, session)

        # Getting all the data from logmytime
        TimeEntries = getAllTimeEntries(logmytime)
        Users = logmytime.entity_sets.Users.get_entities().execute()
        Projects = logmytime.entity_sets.Projects.get_entities().execute()
        Tasks = logmytime.entity_sets.Tasks.get_entities().execute()

        resList = groupAllByTimeEntry(TimeEntries, Users, Projects, Tasks)

        filterListForApril = filterResListByDate(resList, year=2020, month=4)
        test = getSumOfTimeEntriesOfAllProjectsInHours(filterListForApril)

        printProjectHours(test)
        print()
        print()

        for user in Users:
            print(user.FirstName, user.LastName)
            res = filterResListByUser(user.ID, filterListForApril)
            projectHours = getSumOfTimeEntriesOfAllProjectsInHours(res)
            printProjectHours(projectHours)
            print()
            print()

    elif sys.argv[1] == "help":
        print("first arg controlling and second the token")
    else:
        print('This command is not supported, valid options are: controlling and as second argument the token')
